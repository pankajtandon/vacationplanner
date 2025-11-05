package com.technochord.ai.vacationplanner.controller;

import com.technochord.ai.vacationplanner.service.interactive.ConfirmableToolChatService;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/ai")
@Log4j2
public class ConfirmableAIController {

    private final ConfirmableToolChatService chatService;

    public ConfirmableAIController(ConfirmableToolChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            org.springframework.ai.chat.model.ChatResponse chatResponse = chatService.chat(request.getMessage(), request.getUserSuppliedTopK(), request.getModelName());

            // Check if response contains confirmation request
            String textResponse = chatResponse.getResults().get(0).getOutput().getText();
            List<AssistantMessage.ToolCall> toolCallList = chatResponse.getResults().get(0).getOutput().getToolCalls();
            if (textResponse.contains("Conversation ID:")) {
                return ResponseEntity.ok(ChatResponse.confirmationNeeded(textResponse, toolCallList));
            }

            return ResponseEntity.ok(ChatResponse.finalResponse(textResponse));

        } catch (Exception e) {
            log.error("Error processing chat", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ChatResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/confirm-tool")
    public ResponseEntity<ChatResponse> confirmTool(@RequestBody ToolConfirmRequest request) {
        try {
            String response = chatService.confirmTool(
                    request.getConversationId(),
                    request.isApproved(),
                    request.getFeedback(),
                    request.getModelName()
            );

            // Check if more confirmations needed
            if (response.contains("Conversation ID:")) {
                return ResponseEntity.ok(ChatResponse.confirmationNeeded(response, null));
            }

            return ResponseEntity.ok(ChatResponse.finalResponse(response));

        } catch (Exception e) {
            log.error("Error confirming tool", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ChatResponse.error(e.getMessage()));
        }
    }
}

@Data
class ChatRequest {
    private String message;
    private int userSuppliedTopK;
    private String modelName;
}

@Data
class ToolConfirmRequest {
    private String conversationId;
    private boolean approved;
    private String feedback;
    private String modelName;
}

@Data
@Builder
@ToString
class ChatResponse {
    private String response;
    private boolean needsConfirmation;
    private String conversationId;
    private String error;
    private List<AssistantMessage.ToolCall> toolCallList;

    public static ChatResponse confirmationNeeded(String response, List<AssistantMessage.ToolCall> toolCallList) {
        String convId = extractConversationId(response);
        return ChatResponse.builder()
                .response(response)
                .needsConfirmation(true)
                .conversationId(convId)
                .toolCallList(toolCallList)
                .build();
    }

    public static ChatResponse finalResponse(String response) {
        return ChatResponse.builder()
                .response(response)
                .needsConfirmation(false)
                .build();
    }

    public static ChatResponse error(String error) {
        return ChatResponse.builder()
                .error(error)
                .build();
    }

    private static String extractConversationId(String response) {
        if (response.contains("[Conversation ID:")) {
            int start = response.indexOf("[Conversation ID:") + 17;
            int end = response.indexOf("]", start);
            if (end > start) {
                return response.substring(start, end).trim();
            }
        }
        return null;
    }
}
