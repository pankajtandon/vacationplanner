package com.technochord.ai.vacationplanner.config;


//The introduction of the Anthropic models cause two ChatClients (one for OpenAI and the other for Anthropic)
//This needs disabling the autoconfig of the ChatClients.
//Disabling auto means that the ToolCallbacks have to be configured manually.
//I tried doing that (Nov 2025) but doesn't work. There are 2 issues that suggest that this is buggy:
//https://github.com/spring-projects/spring-ai/issues/4169
//and
//https://github.com/spring-projects/spring-ai/issues/4601
//Therefore reverting on supporting 2 model providers for now.
//Will still support > 1 model per provider for now (Will stick with OpenAI).

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.ai.model.anthropic.autoconfigure.AnthropicChatProperties;
import org.springframework.ai.model.anthropic.autoconfigure.AnthropicConnectionProperties;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

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

    @Bean
    public AnthropicApi anthropicApi() {
        return AnthropicApi.builder().apiKey(anthropicConnectionProperties.getApiKey()).build();
    }

    @Bean
    public AnthropicChatModel anthropicChatModel(AnthropicApi anthropicApi) {
        AnthropicChatOptions options = AnthropicChatOptions.builder()
                .model(anthropicChatProperties.getOptions().getModel())
                .internalToolExecutionEnabled(false)
                .temperature(anthropicChatProperties.getOptions().getTemperature())
                .maxTokens(anthropicChatProperties.getOptions().getMaxTokens())
                .build();
        return new AnthropicChatModel(anthropicApi, options, toolCallingManager, RetryUtils.DEFAULT_RETRY_TEMPLATE, observationRegistry);
    }
}
