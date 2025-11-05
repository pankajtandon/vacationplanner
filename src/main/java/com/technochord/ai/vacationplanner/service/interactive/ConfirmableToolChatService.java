package com.technochord.ai.vacationplanner.service.interactive;

import com.technochord.ai.vacationplanner.config.properties.RagProperties;
import com.technochord.ai.vacationplanner.model.interactive.PlannerChatResponse;
import com.technochord.ai.vacationplanner.service.RagService;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;

import java.util.ArrayList;
import java.util.Set;

@Log4j2
public class ConfirmableToolChatService {

    private final ChatClient openAiChatClient;
    //private final ChatClient anthropicChatClient;
    private final RagService ragService;
    private RagProperties ragProperties;
    private String SYSTEM_MESSAGE = "Try to use all the tools at your disposal to answer the question(s) asked.";

    public ConfirmableToolChatService(
            ChatClient openAiChatClient,
            //ChatClient anthropicChatClient,
            RagService ragService,
            RagProperties ragProperties) {
        this.openAiChatClient = openAiChatClient;
        //this.anthropicChatClient = anthropicChatClient;
        this.ragService = ragService;
        this.ragProperties = ragProperties;

    }

    public PlannerChatResponse chat(String userMessage, int userSuppliedTopK, String modelName) throws  Exception {
        log.info("Model being used is " + modelName);
        Set<String> relevantToolNameList = this.ragService.getRagCandidateFunctionNameSet(userMessage,
                userSuppliedTopK == 0 ? ragProperties.topK : userSuppliedTopK);
        if (determineModelProvider(modelName) == ModelProvider.OPEN_AI) {
            ChatOptions runtimeChatOptions = null;
            if (modelName.toLowerCase().startsWith("gpt-5")) {
                //GPT-5-* no longer supports configurable temp!
                runtimeChatOptions = ChatOptions.builder().model(modelName).temperature(1.0).build();
            } else {
                runtimeChatOptions = ChatOptions.builder().model(modelName).build();
            }
            ChatResponse chatResponse =  openAiChatClient.prompt()
                    .user(userMessage)
                    .system(SYSTEM_MESSAGE)
                    .toolNames(relevantToolNameList.toArray(new String[0]))
                    //Uncommenting below causes the auto-configured chatClient to forget the ToolCallbacks. See comment on AnthropicConfig class
                    //.options(runtimeChatOptions)
                    .call()
                    .chatResponse();
            log.debug("Response in service " + chatResponse);

            PlannerChatResponse plannerChatResponse = null;
            if (chatResponse.getResults() != null && chatResponse.getResults().get(0) != null
                    && chatResponse.getResults().size() > 0
                    && chatResponse.getResults().get(0).getOutput() != null) {
                plannerChatResponse = PlannerChatResponse.buildResponse(chatResponse.getResults().get(0).getOutput().getText(),
                        chatResponse.getResults().get(0).getOutput().getToolCalls(), new ArrayList<>(relevantToolNameList));
            }

            log.debug("PlannerChatResponse in service " + plannerChatResponse);
            return plannerChatResponse;

        } else if (determineModelProvider(modelName) == ModelProvider.ANTHROPIC) {
// See comment on AnthropicConfig class
//            return anthropicChatClient.prompt()
//                    .user(userMessage)
//                    .system(SYSTEM_MESSAGE)
//                    .toolNames(relevantToolNameList.toArray(new String[0]))
//                    .options(ChatOptions.builder().model(modelName).maxTokens(4000).build())
//                    .call()
//                    .content();
            throw new IllegalAccessException(String.format("ModelName %s is not supported (yet!! ;) )", modelName));
        } else {
            throw new IllegalAccessException(String.format("ModelName %s is not supported (yet!! ;) )", modelName));
        }
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
                    //Uncommenting below causes the auto-configured chatClient to forget the ToolCallbacks. See comment on AnthropicConfig class
                    //.options(ChatOptions.builder().model(modelName).build())
                    .call()
                    .content();
        } else if (determineModelProvider(modelName) == ModelProvider.ANTHROPIC) {
// See comment on AnthropicConfig class
//            return anthropicChatClient.prompt()
//                    .user("User response")
//                    .system(SYSTEM_MESSAGE)
//                    .advisors(advisorSpec -> advisorSpec
//                            .param("conversationId", conversationId)
//                            .param("approved", approved)
//                            .param("feedback", (feedback == null ? "none" : feedback)))
//                    .options(ChatOptions.builder().model(modelName).build())
//                    .call()
//                    .content();
            throw new IllegalAccessException(String.format("ModelName %s is not supported (yet!! ;) )", modelName));
        } else {
            throw new IllegalAccessException(String.format("ModelName %s is not supported (yet!! ;) )", modelName));
        }
    }


    private ModelProvider determineModelProvider(String modelName) throws Exception {
        if (modelName.toLowerCase().startsWith("gpt")) {
            return ModelProvider.OPEN_AI;
        } else if (modelName.toLowerCase().startsWith("claude")) {
            return ModelProvider.ANTHROPIC;
        } else {
            throw new IllegalAccessException(String.format("ModelName %s is not supported (yet!! ;) )", modelName));
        }
    }

     enum ModelProvider {
        OPEN_AI, ANTHROPIC;
    }
}