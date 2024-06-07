package com.technochord.ai.vacationplanner.config;

import org.springframework.ai.autoconfigure.openai.OpenAiChatProperties;
import org.springframework.ai.autoconfigure.openai.OpenAiConnectionProperties;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.function.FunctionCallbackContext;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;

@Profile("openai")
@Configuration
public class OpenAiConfig {
    @Bean
    public OpenAiApi openAiApi() {
        return new OpenAiApi(StringUtils.hasText(openAiChatProperties.getApiKey()) ? openAiChatProperties.getApiKey() : openAiConnectionProperties.getApiKey());
    }

    @Autowired
    private FunctionCallbackContext functionCallbackContext;

    @Autowired
    private OpenAiChatProperties openAiChatProperties;
    @Autowired
    private OpenAiConnectionProperties openAiConnectionProperties;

    @Primary
    @Bean
    public ChatModel chatModel() {
        ChatModel openAiChatModel = new OpenAiChatModel(openAiApi(), OpenAiChatOptions.builder()
                .withModel(openAiChatProperties.getOptions().getModel())
                .withFunction("currencyExchangeService")
                .withFunction("airfareService")
                .withFunction("weatherService")
                .build(),
                functionCallbackContext,
                RetryUtils.DEFAULT_RETRY_TEMPLATE);
        return openAiChatModel;
    }
}
