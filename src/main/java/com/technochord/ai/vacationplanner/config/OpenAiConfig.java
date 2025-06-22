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
import org.springframework.ai.tool.resolution.ToolCallbackResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

//@Profile("openai")
@Configuration
public class OpenAiConfig {
//    @Bean
//    public OpenAiApi openAiApi() {
//        //return new OpenAiApi(StringUtils.hasText(openAiChatProperties.getApiKey()) ? openAiChatProperties.getApiKey() : openAiConnectionProperties.getApiKey());
//        return new OpenAiApi(openAiChatProperties.getBaseUrl(), openAiChatProperties.getApiKey());
//    }

    @Autowired
    private ToolCallbackResolver toolCallbackResolver;

    @Autowired
    private ObservationRegistry observationRegistry;

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
                .temperature(openAiChatProperties.getOptions().getTemperature())
                //.maxTokens(200)
                .build();
        var chatModel = new OpenAiChatModel(openAiApi, openAiChatOptions, toolCallingManager, RetryUtils.DEFAULT_RETRY_TEMPLATE, observationRegistry);

//        OpenAiChatModel chatModel = new OpenAiChatModel(
//                openAiApi(),
//                OpenAiChatOptions.builder()
//                .model(openAiChatProperties.getOptions().getModel())
//                .temperature(openAiChatProperties.getOptions().getTemperature())
//                //.withFunctions(Set.of("currencyExchangeService", "airfareService", "weatherService", "financialService"))
//                .build(),
//                toolCallbackResolver,
//                RetryUtils.DEFAULT_RETRY_TEMPLATE
//        );
//

        return chatModel;
    }
}
