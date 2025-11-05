package com.technochord.ai.vacationplanner.service.interactive;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.ToolCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Log4j2
public class ToolConfirmationAdvisor implements CallAdvisor {

    private final ConversationStateManager stateManager;
    private final List<ToolCallback> availableToolList;
    private final int order = 0; // High priority

    public ToolConfirmationAdvisor(ConversationStateManager stateManager, List<ToolCallback> availableToolList) {
        this.stateManager = stateManager;
        this.availableToolList = availableToolList;
    }

    @Override
    public String getName() {
        return "ToolConfirmationAdvisor";
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain chain) {
        log.info("ToolConfirmationAdvisor intercepting call");

        // Check if this is a continuation of a conversation with pending tool confirmations
        String conversationId = extractConversationId(chatClientRequest);
        log.debug("Retrieved conversationId {}", conversationId);
        if (conversationId != null) {
            ConversationState state = stateManager.getState(conversationId);
            if (state != null && state.isPendingConfirmation()) {
                // We're in confirmation mode, handle specially
                return handlePendingConfirmation(chatClientRequest, state, chain);
            }
        }

        // First pass - let LLM determine what tools it wants to use
        ChatClientResponse response = chain.nextCall(chatClientRequest);
        log.debug("Retrieved response after processing ToolConfirmationAdvisor: {}", response);
        // Check if response has tool calls
        if (hasToolCalls(response)) {
            // Intercept and request user confirmation
            return interceptToolCallsForConfirmation(chatClientRequest, response);
        }

        // No tools, return normal response
        return response;
    }

    private boolean hasToolCalls(ChatClientResponse response) {
        ChatResponse chatResponse = response.chatResponse();
        if (chatResponse != null && chatResponse.getResult() != null) {
            AssistantMessage message = chatResponse.getResult().getOutput();
            return message.getToolCalls() != null && !message.getToolCalls().isEmpty();
        }
        return false;
    }

    private ChatClientResponse interceptToolCallsForConfirmation(
            ChatClientRequest chatClientRequest,
            ChatClientResponse response) {

        // Extract tool calls from response
        List<AssistantMessage.ToolCall> toolCalls = response.chatResponse()
                .getResult()
                .getOutput()
                .getToolCalls();

        // Create conversation state for confirmation flow
        String conversationId = UUID.randomUUID().toString();
        ConversationState state = new ConversationState();
        state.setConversationId(conversationId);
        state.setOriginalRequest(chatClientRequest);
        state.setToolCalls(toolCalls);
        state.setCurrentToolIndex(0);
        state.setToolResults(new ArrayList<>());
        state.setPendingConfirmation(true);
        state.setOriginalResponse(response);

        stateManager.saveState(state);

        // Create a response that asks for confirmation
        ChatResponse confirmationResponse = createConfirmationResponse(state);

        return new ChatClientResponse(confirmationResponse, response.context());
    }

    private ChatClientResponse handlePendingConfirmation(
            ChatClientRequest chatClientRequest,
            ConversationState state,
            CallAdvisorChain chain) {

        // Get confirmation decision from the request
        log.debug("Handling confirmation request with conversationId {}", state.getConversationId());
        boolean approved = extractConfirmationDecision(chatClientRequest);
        AssistantMessage.ToolCall currentTool = state.getToolCalls().get(state.getCurrentToolIndex());

        if (approved) {
            // Execute the tool
            String result = executeToolDirectly(currentTool);
            state.getToolResults().add(new ToolExecutionResult(currentTool, result, true));
            log.info("Tool {} executed: {}", currentTool.name(), result);
        } else {
            // Skip the tool
            String feedback = extractUserFeedback(chatClientRequest);
            state.getToolResults().add(new ToolExecutionResult(
                    currentTool,
                    "Skipped by user" +
                            (feedback != null ? ": " + feedback : ""),
                    false
            ));
            log.info("Tool {} skipped by user", currentTool.name());
        }

        // Move to next tool
        state.setCurrentToolIndex(state.getCurrentToolIndex() + 1);

        // Check if more tools need confirmation
        if (state.getCurrentToolIndex() < state.getToolCalls().size()) {
            // More tools to confirm
            stateManager.updateState(state);
            ChatResponse nextConfirmation = createConfirmationResponse(state);
            return new ChatClientResponse(nextConfirmation, chatClientRequest.context());
        } else {
            // All tools processed, generate final response
            state.setPendingConfirmation(false);
            stateManager.updateState(state);
            return generateFinalResponseWithAdvisor(state, chatClientRequest, chain);
        }
    }

    private String executeToolDirectly(AssistantMessage.ToolCall toolCall) {
        ToolCallback callback = availableToolList.stream().filter(tc -> tc.getToolDefinition()
                .name().equals(toolCall.name())).findFirst().orElse(null);
        if (callback != null) {
            try {
                return callback.call(toolCall.arguments());
            } catch (Exception e) {
                log.error("Error executing tool {}", toolCall.name(), e);
                return "Error: " + e.getMessage();
            }
        }

        return "Tool not found: " + toolCall.name();
    }

    private ChatResponse createConfirmationResponse(ConversationState state) {
        AssistantMessage.ToolCall currentTool = state.getToolCalls().get(state.getCurrentToolIndex());

        String confirmationMessage = String.format("""
            Tool Confirmation Required
            Tool %d of %d: %s
            Arguments:
            %s
            Previous Results:
            %s
            Do you want to execute this tool?
            [Conversation ID: %s]
            """,
                state.getCurrentToolIndex() + 1,
                state.getToolCalls().size(),
                currentTool.name(),
                formatArguments(currentTool.arguments()),
                formatPreviousResults(state.getToolResults()),
                state.getConversationId()
        );

        AssistantMessage message = new AssistantMessage(confirmationMessage, state.getOriginalResponse().context(), state.getToolCalls());
        Generation generation = new Generation(message);
        ChatResponse response = new ChatResponse(List.of(generation));

        return response;
    }

    private ChatClientResponse generateFinalResponseWithAdvisor(
            ConversationState state,
            ChatClientRequest chatClientRequest,
            CallAdvisorChain chain) {

        // Build context with tool results
        StringBuilder context = new StringBuilder();
        context.append("Tool Execution Summary:\n");
        for (ToolExecutionResult result : state.getToolResults()) {
            context.append(String.format("- %s: %s (executed: %s)\n",
                    result.getToolCall().name(),
                    result.getResult(),
                    result.isExecuted() ? "Y" : "N"
            ));
        }

        // Create new request with tool results as context
        String originalUserMessage = extractOriginalUserMessage(state.getOriginalRequest());
        String finalPrompt = String.format("""
            Original request: %s
            %s
            Based on the tool execution results, provide a comprehensive response.
            Acknowledge any skipped tools and work with the available information.
            """,
                originalUserMessage,
                context.toString()
        );

        // Create new advised request without tool confirmation context
        ChatClientRequest finalRequest = chatClientRequest.builder()
                .prompt(new Prompt(finalPrompt))
                .context(Map.of("SKIP_TOOL_CONFIRMATION", true))
                .build();
        log.debug("Final request: " + finalRequest);
        // Clean up state
        stateManager.deleteState(state.getConversationId());

        // Call through chain to get final response
        return chain.nextCall(finalRequest);
    }

    private String extractConversationId(ChatClientRequest request) {
        // Extract from user params or message
        Map<String, Object> params = request.context();
        if (params.containsKey("conversationId")) {
            return (String) params.get("conversationId");
        }
//PT: See how we can further parse the message as done below. Spring 1.0.3 no longer has a request.userText() call
        // Try to extract from user message
        String userText = request.prompt().getContents(); // Changed from request.userText() that is no longet supported
        if (userText != null && userText.contains("[Conversation ID:")) {
            int start = userText.indexOf("[Conversation ID:") + 17;
            int end = userText.indexOf("]", start);
            if (end > start) {
                return userText.substring(start, end).trim();
            }
        }

        return null;
    }

    private boolean extractConfirmationDecision(ChatClientRequest request) {
        Map<String, Object> params = request.context();
        if (params.containsKey("approved")) {
            return (Boolean) params.get("approved");
        }

        // Try to parse from user message
        String userText = request.prompt().getContents().toLowerCase(); // Changed from request.userText() that is no longet supported
        return userText.contains("yes") || userText.contains("approve") || userText.contains("confirm");
    }

    private String extractUserFeedback(ChatClientRequest request) {
        Map<String, Object> params = request.context();
        return (String) params.get("feedback");
    }

    private String extractOriginalUserMessage(ChatClientRequest request) {
        List<Message> messages = request.prompt().getInstructions(); //PT: Changed from request.messages()
        return messages.stream()
                .filter(m -> m instanceof UserMessage)
                .map(Message::getText)
                .findFirst()
                .orElse("");
    }

    private String formatArguments(String arguments) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object json = mapper.readValue(arguments, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (Exception e) {
            return arguments;
        }
    }

    private String formatPreviousResults(List<ToolExecutionResult> results) {
        if (results.isEmpty()) {
            return "None yet";
        }

        StringBuilder sb = new StringBuilder();
        for (ToolExecutionResult result : results) {
            sb.append(String.format("  %s %s: %s\n",
                    result.isExecuted() ? "Y" : "N",
                    result.getToolCall().name(),
                    result.getResult()
            ));
        }
        return sb.toString();
    }
}

