package com.technochord.ai.vacationplanner.config;

import com.technochord.ai.vacationplanner.service.interactive.ToolConfirmationAdvisor;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.ai.model.deepseek.autoconfigure.DeepSeekChatProperties;
import org.springframework.ai.model.deepseek.autoconfigure.DeepSeekConnectionProperties;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.List;

@Configuration
public class DeepSeekConfig {

    @Autowired
    private DeepSeekConnectionProperties deepSeekConnectionProperties;

    @Autowired
    private DeepSeekChatProperties deepSeekChatProperties;

    @Autowired
    private ObservationRegistry observationRegistry;

    @Lazy
    @Autowired
    private ToolCallingManager toolCallingManager;

    @Lazy
    @Autowired
    private ToolConfirmationAdvisor toolConfirmationAdvisor;

    @Bean
    public ChatClient deepSeekChatClient() {
        return ChatClient.builder(deepSeekChatModel())
                .defaultAdvisors(List.of(toolConfirmationAdvisor))
                .build();
    }

    private DeepSeekChatModel deepSeekChatModel() {
        DeepSeekApi deepSeekApi = DeepSeekApi.builder().apiKey(deepSeekConnectionProperties.getApiKey()).build();
        DeepSeekChatOptions options = DeepSeekChatOptions.builder()
                .model(deepSeekChatProperties.getOptions().getModel())
                .internalToolExecutionEnabled(false)
                .temperature(deepSeekChatProperties.getOptions().getTemperature())
                .maxTokens(deepSeekChatProperties.getOptions().getMaxTokens())
                .build();
        return new DeepSeekChatModel(deepSeekApi, options, toolCallingManager, RetryUtils.DEFAULT_RETRY_TEMPLATE, observationRegistry);
    }
}
