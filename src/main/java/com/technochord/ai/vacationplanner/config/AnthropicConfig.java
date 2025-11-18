package com.technochord.ai.vacationplanner.config;

import com.technochord.ai.vacationplanner.service.interactive.ToolConfirmationAdvisor;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.anthropic.autoconfigure.AnthropicChatProperties;
import org.springframework.ai.model.anthropic.autoconfigure.AnthropicConnectionProperties;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.List;

@Configuration
public class AnthropicConfig {

    @Autowired
    private AnthropicConnectionProperties anthropicConnectionProperties;

    @Autowired
    private AnthropicChatProperties anthropicChatProperties;

    @Autowired
    private ObservationRegistry observationRegistry;

    @Lazy
    @Autowired
    private ToolCallingManager toolCallingManager;
    @Lazy
    @Autowired
    private ToolConfirmationAdvisor toolConfirmationAdvisor;

    @Bean
    public ChatClient anthropicChatClient() {
        return ChatClient.builder(anthropicChatModel())
                .defaultAdvisors(List.of(toolConfirmationAdvisor))
                .build();
    }

    private AnthropicChatModel anthropicChatModel() {
        AnthropicApi anthropicApi = AnthropicApi.builder().apiKey(anthropicConnectionProperties.getApiKey()).build();
        AnthropicChatOptions options = AnthropicChatOptions.builder()
                .model(anthropicChatProperties.getOptions().getModel())
                .internalToolExecutionEnabled(false)
                .temperature(anthropicChatProperties.getOptions().getTemperature())
                .maxTokens(anthropicChatProperties.getOptions().getMaxTokens())
                .build();
        return new AnthropicChatModel(anthropicApi, options, toolCallingManager, RetryUtils.DEFAULT_RETRY_TEMPLATE, observationRegistry);
    }
}
