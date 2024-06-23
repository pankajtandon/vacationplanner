package com.technochord.ai.vacationplanner.config;

import com.technochord.ai.vacationplanner.config.properties.CurrencyExchangeProperties;
import com.technochord.ai.vacationplanner.config.properties.FlightProperties;
import com.technochord.ai.vacationplanner.config.properties.WeatherProperties;
import com.technochord.ai.vacationplanner.service.*;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.function.Function;

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
    private CurrencyExchangeProperties currencyExchangeProperties;

    @Bean
    public Function<WeatherService.Request, WeatherService.Response> weatherService() {
        return new WeatherService(restTemplate, weatherProperties);
    }

    @Bean
    public Function<AirfareService.Request, AirfareService.Response> airfareService() {
        return new AirfareService(flightProperties, restTemplate);
    }

    @Bean
    public Function<CurrencyExchangeService.Request, CurrencyExchangeService.Response> currencyExchangeService() {
        return new CurrencyExchangeService(currencyExchangeProperties, restTemplate);
    }

    @Bean
    public Function<FinancialService.Request, FinancialService.Response> financialService() {
        return new FinancialService();
    }

    @Bean
    public VacationService vacationService(final ChatModel model) {
        return new VacationService(model);
    }
}
