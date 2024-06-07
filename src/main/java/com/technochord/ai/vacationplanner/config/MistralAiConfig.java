package com.technochord.ai.vacationplanner.config;

import org.springframework.ai.autoconfigure.mistralai.MistralAiChatProperties;
import org.springframework.ai.autoconfigure.mistralai.MistralAiCommonProperties;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.ai.mistralai.MistralAiChatOptions;
import org.springframework.ai.mistralai.api.MistralAiApi;
import org.springframework.ai.model.function.FunctionCallbackContext;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;

@Profile("mistralai")
@Configuration
public class MistralAiConfig {
    @Bean
    public MistralAiApi mistralAiApi() {
        return new MistralAiApi(StringUtils.hasText(mistralAiChatProperties.getApiKey()) ? mistralAiChatProperties.getApiKey() : mistralAiCommonProperties.getApiKey());
    }

    @Autowired
    private FunctionCallbackContext functionCallbackContext;

    @Autowired
    private MistralAiChatProperties mistralAiChatProperties;

    @Autowired
    private MistralAiCommonProperties mistralAiCommonProperties;

    @Primary
    @Bean
    public ChatModel chatModel() {
        ChatModel mistralChatModel = new MistralAiChatModel(mistralAiApi(), MistralAiChatOptions.builder()
                .withModel(mistralAiChatProperties.getOptions().getModel())
                .withFunction("currencyExchangeService")
                .withFunction("airfareService")
                .withFunction("weatherService")
                .build(),
                functionCallbackContext,
                RetryUtils.DEFAULT_RETRY_TEMPLATE);

        return mistralChatModel;
    }
}
