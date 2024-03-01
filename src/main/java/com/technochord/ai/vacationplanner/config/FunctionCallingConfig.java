package com.technochord.ai.vacationplanner.config;

import com.technochord.ai.vacationplanner.service.AirfareService;
import com.technochord.ai.vacationplanner.service.CurrencyExchangeService;
import com.technochord.ai.vacationplanner.service.VacationService;
import com.technochord.ai.vacationplanner.service.WeatherService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class FunctionCallingConfig {
    @Bean
    public Function<WeatherService.Request, WeatherService.Response> weatherService() {
        return new WeatherService();
    }

    @Bean
    public Function<AirfareService.Request, AirfareService.Response> airfareService() {
        return new AirfareService();
    }

    @Bean
    public Function<CurrencyExchangeService.Request, CurrencyExchangeService.Response> currencyExchangeService() {
        return new CurrencyExchangeService();
    }

    @Bean
    public VacationService openAIService() {
        return new VacationService();
    }
}
