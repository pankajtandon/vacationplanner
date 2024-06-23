package com.technochord.ai.vacationplanner.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.List;

@Log4j2
public class VacationService {

    private ChatModel chatModel;
    public VacationService(final ChatModel model) {
        this.chatModel = model;
    }

    public String planVacation(final String message) {
        UserMessage userMessage = new UserMessage(message);

        ChatResponse response = chatModel.call(new Prompt(List.of(userMessage)));

        return response.getResult().getOutput().getContent();
    }
}
