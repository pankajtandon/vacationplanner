package com.technochord.ai.vacationplanner.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
public class CurrencyExchangeServiceTests {

    @Autowired
    private CurrencyExchangeService currencyExchangeService;

    @Test
    public void testExchangeRate() {
        double euros = currencyExchangeService.getExchange("USD", "EUR", 2.0);

        Assert.notNull(euros, "Euros cannot be null");
    }
}
