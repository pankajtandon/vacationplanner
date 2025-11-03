package com.technochord.ai.vacationplanner.service.interactive;

import com.technochord.ai.vacationplanner.config.properties.RagProperties;
import com.technochord.ai.vacationplanner.service.RagService;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.Set;

@Log4j2
public class ConfirmableToolChatService {

    private final ChatClient chatClient;
    private final RagService ragService;
    private RagProperties ragProperties;

    public ConfirmableToolChatService(
            ChatClient.Builder chatClientBuilder,
            ToolConfirmationAdvisor confirmationAdvisor,
            List<ToolCallback> tools,
            RagService ragService,
            RagProperties ragProperties) {
        this.ragService = ragService;
        this.ragProperties = ragProperties;

        this.chatClient = chatClientBuilder
                .defaultAdvisors(List.of(confirmationAdvisor))
                .defaultToolCallbacks(tools)
                .build();
    }

    public String chat(String userMessage, int userSuppliedTopK) {
        Set<String> relevantToolNameList = this.ragService.getRagCandidateFunctionNameSet(userMessage,
                userSuppliedTopK == 0 ? ragProperties.topK : userSuppliedTopK);
        return chatClient.prompt()
                .user(userMessage)
                .toolNames(relevantToolNameList.toArray(new String[0]))
                .call()
                .content();
    }

    public String confirmTool(String conversationId, boolean approved, String feedback) {
        return chatClient.prompt()
                .user("User response")
                .advisors(advisorSpec -> advisorSpec
                        .param("conversationId", conversationId)
                        .param("approved", approved)
                        .param("feedback", feedback))
                .call()
                .content();
    }
}