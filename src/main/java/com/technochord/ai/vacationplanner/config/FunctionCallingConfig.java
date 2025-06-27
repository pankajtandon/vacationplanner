package com.technochord.ai.vacationplanner.config;

import com.technochord.ai.vacationplanner.config.properties.CurrencyExchangeProperties;
import com.technochord.ai.vacationplanner.config.properties.FlightProperties;
import com.technochord.ai.vacationplanner.config.properties.RagProperties;
import com.technochord.ai.vacationplanner.config.properties.WeatherProperties;
import com.technochord.ai.vacationplanner.service.*;
import org.springframework.ai.chat.model.ChatModel;
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
public class FunctionCallingConfig {

    @Autowired
    private ChatModel chatModel;

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
    public List<ToolCallback> localTools(final WeatherService weatherService, final AirfareService airfareService,
                                         final CurrencyExchangeService currencyExchangeService,
                                         final FinancialService financialService,
                                         final RecipeService recipeService) {
        return List.of(ToolCallbacks.from(weatherService, airfareService, currencyExchangeService, financialService, recipeService));
    }

    @Bean
    public RagService ragService() {
        List<ToolCallback> toolCallbackList = new ArrayList<>();
        //First get the local functions/tools
        List<ToolCallback> methodToolCallbackList = localTools(weatherService(), airfareService(), currencyExchangeService(),
                financialService(), recipeService());
        if (methodToolCallbackList != null) {
            toolCallbackList.addAll(methodToolCallbackList);
        }

        //Next get the MCP tools...
        ToolCallback[] mcpToolCallbackArray =  syncMcpToolCallbackProvider.getToolCallbacks();
        if (mcpToolCallbackArray != null) {
            toolCallbackList.addAll(Arrays.stream(mcpToolCallbackArray).toList());
        }
        return new RagService(ragCandidateServiceContext(), vectorStore,
                toolCallbackList,
                ragProperties);
    }
    @Bean
    public VacationService vacationService() {
        return new VacationService(chatModel, ragService());
    }

    @Bean
    public RagCandidateSpringContext ragCandidateServiceContext() {
        return new RagCandidateSpringContext();
    }
}
