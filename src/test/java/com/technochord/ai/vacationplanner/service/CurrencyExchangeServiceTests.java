package com.technochord.ai.vacationplanner.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperties;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
@EnabledIfSystemProperties( value = {
        @EnabledIfSystemProperty(named = "spring.ai.openai.apiKey", matches = ".*"),
        @EnabledIfSystemProperty(named = "weather.visualcrossing.apiKey", matches = ".*"),
        @EnabledIfSystemProperty(named = "flight.amadeus.client-id", matches = ".*"),
        @EnabledIfSystemProperty(named = "flight.amadeus.client-secret", matches = ".*")
})
public class CurrencyExchangeServiceTests {

    @Autowired
    private CurrencyExchangeService currencyExchangeService;

    @Test
    public void testExchangeRate() {
        double euros = currencyExchangeService.getExchange("USD", "EUR", 2.0);

        Assert.notNull(euros, "Euros cannot be null");
    }
}
