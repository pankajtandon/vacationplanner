package com.technochord.ai.vacationplanner.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;

import java.util.List;
import java.util.Set;

@Log4j2
public class VacationService {

    private ChatModel chatModel;

    private RagService ragService;

    public VacationService(final ChatModel model,
                           final RagService ragService) {
        this.chatModel = model;
        this.ragService = ragService;
    }

    public String planVacation(final String message, final int userSuppliedTopK) {
        UserMessage userMessage = new UserMessage(message);
        SystemMessage systemMessage = new SystemMessage("Format the response using markdown. Make assumptions, without asking clarifying questions.");
        Set<String> ragBeans = ragService.getRagCandidateFunctionNameSet(userMessage.getText(), userSuppliedTopK);

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage),
                OpenAiChatOptions.builder()
                .toolNames(ragBeans).build());

        ChatResponse response = chatModel.call(prompt);
        log.info("Returned a recommendation!");
        return response.getResult().getOutput().getText();
    }
}
