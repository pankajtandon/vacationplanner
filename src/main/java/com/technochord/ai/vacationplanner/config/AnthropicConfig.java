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

//@Configuration
public class AnthropicConfig {

//    @Autowired
//    private AnthropicConnectionProperties anthropicConnectionProperties;
//
//    @Autowired
//    private AnthropicChatProperties anthropicChatProperties;
//
//    @Autowired
//    private ObservationRegistry observationRegistry;
//
//    @Lazy
//    @Autowired
//    private ToolCallingManager toolCallingManager;
//
//    @Bean
//    public AnthropicApi anthropicApi() {
//        return AnthropicApi.builder().apiKey(anthropicConnectionProperties.getApiKey()).build();
//    }
//
//    @Bean
//    public AnthropicChatModel anthropicChatModel(AnthropicApi anthropicApi) {
//        AnthropicChatOptions options = AnthropicChatOptions.builder()
//                .model(anthropicChatProperties.getOptions().getModel())
//                .temperature(0.7) // Example: set temperature
//                .build();
//        return new AnthropicChatModel(anthropicApi, options, toolCallingManager, RetryUtils.DEFAULT_RETRY_TEMPLATE, observationRegistry);
//    }
}
