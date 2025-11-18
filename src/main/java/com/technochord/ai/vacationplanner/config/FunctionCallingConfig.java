package com.technochord.ai.vacationplanner.config;

import com.technochord.ai.vacationplanner.config.properties.CurrencyExchangeProperties;
import com.technochord.ai.vacationplanner.config.properties.FlightProperties;
import com.technochord.ai.vacationplanner.config.properties.RagProperties;
import com.technochord.ai.vacationplanner.config.properties.WeatherProperties;
import com.technochord.ai.vacationplanner.service.*;
import com.technochord.ai.vacationplanner.service.interactive.ConfirmableToolChatService;
import com.technochord.ai.vacationplanner.service.interactive.ConversationStateManager;
import com.technochord.ai.vacationplanner.service.interactive.ToolConfirmationAdvisor;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@Log4j2
public class FunctionCallingConfig {

    @Autowired
    private ChatClient openAiChatClient;

    @Autowired
    private ChatClient anthropicChatClient;

    @Autowired
    private ChatClient deepSeekChatClient;


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WeatherProperties weatherProperties;

    @Autowired
    private FlightProperties flightProperties;

    @Autowired
    private RagProperties ragProperties;

    @Autowired
    private CurrencyExchangeProperties currencyExchangeProperties;

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private SyncMcpToolCallbackProvider syncMcpToolCallbackProvider;

    @Bean
    public WeatherService weatherService() {
        return new WeatherService(restTemplate, weatherProperties);
    }

    @Bean
    public AirfareService airfareService() {
        return new AirfareService(flightProperties, restTemplate);
    }

    @Bean
    public CurrencyExchangeService currencyExchangeService() {
        return new CurrencyExchangeService(currencyExchangeProperties, restTemplate);
    }

    @Bean
    public FinancialService financialService() {
        return new FinancialService();
    }

    @Bean
    public RecipeService recipeService() {
        return new RecipeService();
    }

    @Bean
    public BookingService bookingService() {
        return new BookingService();
    }

    @Bean
    public SpendingLogsService spendingLogsService() {
        return new SpendingLogsService();
    }

    @Bean
    public RagService ragService() {
        return new RagService(ragCandidateServiceContext(), vectorStore,
                allAvailableToolsList(weatherService(), airfareService(), currencyExchangeService(), financialService(), recipeService(),
                        bookingService(), spendingLogsService()),
                ragProperties);
    }

    @Bean
    public RagCandidateSpringContext ragCandidateServiceContext() {
        return new RagCandidateSpringContext();
    }


    @Bean
    public PromptChatMemoryAdvisor promptChatMemoryAdvisor() {
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(100)
                .build();
        return PromptChatMemoryAdvisor.builder(chatMemory)
                .order(1)
                .build();
    }

    @Bean
    public ConversationStateManager conversationStateManager() {
        return new ConversationStateManager();
    }


    @Bean
    public ToolConfirmationAdvisor toolConfirmationAdvisor() {
        return new ToolConfirmationAdvisor(conversationStateManager(),ragService());
    }
    @Bean
    public ConfirmableToolChatService confirmableToolChatService() {
        return new ConfirmableToolChatService(openAiChatClient, anthropicChatClient, deepSeekChatClient, ragService(), ragProperties);
    }



    //----- P R I V A T E ---

    private List<ToolCallback> allAvailableToolsList(final WeatherService weatherService, final AirfareService airfareService,
                                                 final CurrencyExchangeService currencyExchangeService,
                                                 final FinancialService financialService,
                                                 final RecipeService recipeService,
                                                 final BookingService bookingService, SpendingLogsService spendingLogsService) {
        List<ToolCallback> toolCallbackList = new ArrayList<>();

        List<ToolCallback> methodToolCallbackList = List.of(ToolCallbacks.from(weatherService, airfareService, currencyExchangeService,
                financialService, recipeService, bookingService, spendingLogsService));

        //First get the method functions/tools
        if (methodToolCallbackList != null) {
            toolCallbackList.addAll(methodToolCallbackList);
        }

        //Next get the MCP tools...
        ToolCallback[] mcpToolCallbackArray =  syncMcpToolCallbackProvider.getToolCallbacks();
        if (mcpToolCallbackArray != null) {
            toolCallbackList.addAll(Arrays.stream(mcpToolCallbackArray).toList());
        }
        log.info("Found a total of {} tools", toolCallbackList.size());
        return toolCallbackList;
    }
}
