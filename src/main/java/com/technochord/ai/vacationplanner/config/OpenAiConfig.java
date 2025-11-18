package com.technochord.ai.vacationplanner.config;

import com.technochord.ai.vacationplanner.service.interactive.ToolConfirmationAdvisor;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.openai.autoconfigure.OpenAiChatProperties;
import org.springframework.ai.model.openai.autoconfigure.OpenAiConnectionProperties;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.List;

@Configuration
public class OpenAiConfig {

    @Autowired
    private ObservationRegistry observationRegistry;

    @Autowired
    private OpenAiChatProperties openAiChatProperties;

    @Autowired
    private OpenAiConnectionProperties openAiConnectionProperties;

    @Lazy
    @Autowired
    private ToolCallingManager toolCallingManager;

    @Lazy
    @Autowired
    private ToolConfirmationAdvisor toolConfirmationAdvisor;


    @Bean
    public ChatClient openAiChatClient() {
        return ChatClient.builder(openAiChatModel())
                .defaultAdvisors(List.of(toolConfirmationAdvisor))
                .build();
    }

    private OpenAiChatModel openAiChatModel() {
        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(openAiConnectionProperties.getApiKey())
                .build();
        var openAiChatOptions = OpenAiChatOptions.builder()
                .model(openAiChatProperties.getOptions().getModel())
                .internalToolExecutionEnabled(false)
                .temperature(openAiChatProperties.getOptions().getTemperature())
                //.maxTokens(200)
                .build();
        var opeAiChatModel = new OpenAiChatModel(openAiApi, openAiChatOptions, toolCallingManager, RetryUtils.DEFAULT_RETRY_TEMPLATE, observationRegistry);


        return opeAiChatModel;
    }
}
