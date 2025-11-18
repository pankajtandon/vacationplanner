package com.technochord.ai.vacationplanner.service.interactive;

import com.technochord.ai.vacationplanner.config.properties.RagProperties;
import com.technochord.ai.vacationplanner.model.interactive.PlannerChatResponse;
import com.technochord.ai.vacationplanner.service.RagService;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.tool.ToolCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
public class ConfirmableToolChatService {

    private final ChatClient openAiChatClient;
    private final ChatClient anthropicChatClient;
    private final ChatClient deepSeekChatClient;
    private final RagService ragService;
    private RagProperties ragProperties;

    private String SYSTEM_MESSAGE = "Use all the tools at your disposal to answer all aspects of the question asked.";

    public ConfirmableToolChatService(
            ChatClient openAiChatClient,
            ChatClient anthropicChatClient,
            ChatClient deepSeekChatClient,
            RagService ragService,
            RagProperties ragProperties) {
        this.openAiChatClient = openAiChatClient;
        this.anthropicChatClient = anthropicChatClient;
        this.ragService = ragService;
        this.ragProperties = ragProperties;
        this.deepSeekChatClient = deepSeekChatClient;
    }

    public PlannerChatResponse chat(String userMessage, int userSuppliedTopK, String modelName, String temperature) throws  Exception {

        ChatResponse chatResponse = null;
        ChatOptions runtimeChatOptions = null;
        List<ToolCallback> filteredToolCallbacks = this.ragService.getRagCandidateToolCallbackList(userMessage,
                userSuppliedTopK == 0 ? ragProperties.topK : userSuppliedTopK);
        List<String> filteredToolNameList = filteredToolCallbacks.stream().map(tc -> tc.getToolDefinition().name()).collect(Collectors.toList());
        log.debug("List of filtered callback names (after RAG analysis) are: " + filteredToolNameList);
        log.info("Model being used is " + modelName);
        if (determineModelProvider(modelName) == ModelProvider.OPEN_AI) {
            //GPT-5-* no longer supports configurable temp!
            Double temp = (modelName.toLowerCase().equals("gpt-5-nano") ? 1.0 : Double.parseDouble(temperature));

            log.info("Model being used is " + modelName + ", temp: " + temp.toString());
            runtimeChatOptions = OpenAiChatOptions.builder().model(modelName).temperature(temp)
                    .toolCallbacks(filteredToolCallbacks).build();
            chatResponse =  openAiChatClient.prompt()
                    .user(userMessage)
                    .system(SYSTEM_MESSAGE)
                    .options(runtimeChatOptions)
                    .call()
                    .chatResponse();
        } else if (determineModelProvider(modelName) == ModelProvider.ANTHROPIC) {
            runtimeChatOptions = AnthropicChatOptions.builder().model(modelName)
                    .temperature(Double.parseDouble(temperature))
                    .toolCallbacks(filteredToolCallbacks).build();
            chatResponse = anthropicChatClient.prompt()
                    .user(userMessage)
                    .system(SYSTEM_MESSAGE)
                    .options(runtimeChatOptions)
                    .call()
                    .chatResponse();
        } else if (determineModelProvider(modelName) == ModelProvider.DEEPSEEK) {
            runtimeChatOptions = DeepSeekChatOptions.builder().model(modelName)
                    .temperature(Double.parseDouble(temperature))
                    .toolCallbacks(filteredToolCallbacks).build();
            chatResponse = deepSeekChatClient.prompt()
                    .user(userMessage)
                    .system(SYSTEM_MESSAGE)
                    .options(runtimeChatOptions)
                    .call()
                    .chatResponse();
        } else {
            throw new IllegalAccessException(String.format("ModelName %s is not supported (yet!! ;) )", modelName));
        }

        PlannerChatResponse plannerChatResponse = null;
        if (chatResponse.getResults() != null && chatResponse.getResults().get(0) != null
                && chatResponse.getResults().size() > 0
                && chatResponse.getResults().get(0).getOutput() != null) {
            plannerChatResponse = PlannerChatResponse.buildResponse(chatResponse.getResults().get(0).getOutput().getText(),
                    chatResponse.getResults().get(0).getOutput().getToolCalls(),
                    new ArrayList<>(filteredToolNameList));
        }

        log.debug("PlannerChatResponse in service " + plannerChatResponse);
        return plannerChatResponse;
    }

    public String confirmTool(String conversationId, boolean approved, String feedback, String modelName) throws Exception {
        if (determineModelProvider(modelName) == ModelProvider.OPEN_AI) {
            return openAiChatClient.prompt()
                    .user("User response")
                    .system(SYSTEM_MESSAGE)
                    .advisors(advisorSpec -> advisorSpec
                            .param("conversationId", conversationId)
                            .param("approved", approved)
                            .param("feedback", (feedback == null ? "none" : feedback)))
                    .options(ChatOptions.builder().model(modelName).build())
                    .call()
                    .content();
        } else if (determineModelProvider(modelName) == ModelProvider.ANTHROPIC) {
            return anthropicChatClient.prompt()
                    .user("User response")
                    .system(SYSTEM_MESSAGE)
                    .advisors(advisorSpec -> advisorSpec
                            .param("conversationId", conversationId)
                            .param("approved", approved)
                            .param("feedback", (feedback == null ? "none" : feedback)))
                    .options(ChatOptions.builder().model(modelName).build())
                    .call()
                    .content();
        } else if (determineModelProvider(modelName) == ModelProvider.DEEPSEEK) {
            return deepSeekChatClient.prompt()
                    .user("User response")
                    .system(SYSTEM_MESSAGE)
                    .advisors(advisorSpec -> advisorSpec
                            .param("conversationId", conversationId)
                            .param("approved", approved)
                            .param("feedback", (feedback == null ? "none" : feedback)))
                    .options(ChatOptions.builder().model(modelName).build())
                    .call()
                    .content();


        } else {
            throw new IllegalAccessException(String.format("ModelName %s is not supported (yet!! ;) )", modelName));
        }
    }


    private ModelProvider determineModelProvider(String modelName) throws Exception {
        if (modelName.toLowerCase().startsWith("gpt")) {
            return ModelProvider.OPEN_AI;
        } else if (modelName.toLowerCase().startsWith("claude")) {
            return ModelProvider.ANTHROPIC;
        } else if (modelName.toLowerCase().startsWith("deepseek")) {
            return ModelProvider.DEEPSEEK;
        } else {
            throw new IllegalAccessException(String.format("ModelName %s is not supported (yet!! ;) )", modelName));
        }
    }

     enum ModelProvider {
        OPEN_AI, ANTHROPIC, DEEPSEEK;
    }
}