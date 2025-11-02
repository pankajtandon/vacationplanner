package com.technochord.ai.vacationplanner.service.interactive;

import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class ConfirmableToolChatService {

    private final ChatClient chatClient;

    public ConfirmableToolChatService(
            ChatClient.Builder chatClientBuilder,
            ToolConfirmationAdvisor confirmationAdvisor,
            List<ToolCallback> tools) {

        this.chatClient = chatClientBuilder
                .defaultAdvisors(List.of(confirmationAdvisor))
                .defaultToolCallbacks(tools)
                .build();
    }

    public String chat(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
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