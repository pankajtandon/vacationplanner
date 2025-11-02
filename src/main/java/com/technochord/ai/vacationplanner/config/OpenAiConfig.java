package com.technochord.ai.vacationplanner.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.model.ChatModel;
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
import org.springframework.context.annotation.Primary;

@Configuration
public class OpenAiConfig {

    @Autowired
    private ObservationRegistry observationRegistry;

    @Lazy
    @Autowired
    private ToolCallingManager toolCallingManager;

    @Autowired
    private OpenAiChatProperties openAiChatProperties;
    @Autowired
    private OpenAiConnectionProperties openAiConnectionProperties;

    @Primary
    @Bean
    public ChatModel chatModel() {
        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(openAiConnectionProperties.getApiKey())
                .build();
        var openAiChatOptions = OpenAiChatOptions.builder()
                .model(openAiChatProperties.getOptions().getModel())
                .internalToolExecutionEnabled(false)
                .temperature(openAiChatProperties.getOptions().getTemperature())
                //.maxTokens(200)
                .build();
        var chatModel = new OpenAiChatModel(openAiApi, openAiChatOptions, toolCallingManager, RetryUtils.DEFAULT_RETRY_TEMPLATE, observationRegistry);


        return chatModel;
    }
}
