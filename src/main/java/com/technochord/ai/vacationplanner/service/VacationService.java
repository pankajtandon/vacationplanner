package com.technochord.ai.vacationplanner.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.function.FunctionCallbackContext;
import org.springframework.ai.openai.OpenAiChatOptions;

import java.util.List;
import java.util.Set;

@Log4j2
public class VacationService {

    private ChatModel chatModel;
    private FunctionCallbackContext functionCallbackContext;

    private RagService ragService;

    public VacationService(final ChatModel model,
                           final FunctionCallbackContext functionCallbackContext,
                           final RagService ragService) {
        this.chatModel = model;
        this.functionCallbackContext = functionCallbackContext;
        this.ragService = ragService;
    }

    public String planVacation(final String message) {
        UserMessage userMessage = new UserMessage(message);
        Set<String> ragBeans = ragService.getRagCandidateFunctionNameSet(userMessage.getContent());

        Prompt prompt = new Prompt(List.of(userMessage), OpenAiChatOptions.builder()
                .withFunctions(ragBeans).build());

        ChatResponse response = chatModel.call(prompt);

        return response.getResult().getOutput().getContent();
    }
}
