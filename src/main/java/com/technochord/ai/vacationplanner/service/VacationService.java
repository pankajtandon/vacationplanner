package com.technochord.ai.vacationplanner.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Log4j2
public class VacationService {
    @Autowired
    private OpenAiChatClient openAiChatClient;
    public String planVacation() {
        UserMessage userMessage = new UserMessage("I live in Pittsburgh, PA and I love golf. " +
                "In the summer of 2024, where should I fly to, in Europe or the United States, to play, where the weather " +
                "is pleasant and it's economical too?");

        ChatResponse response = openAiChatClient.call(new Prompt(List.of(userMessage),
                OpenAiChatOptions.builder()
                        .withFunction("currencyExchangeService")
                        .withFunction("airfareService")
                        .withFunction("weatherService")
                        .build()));

        log.info("Response: {}", response);
        return response.toString();
    }
}
