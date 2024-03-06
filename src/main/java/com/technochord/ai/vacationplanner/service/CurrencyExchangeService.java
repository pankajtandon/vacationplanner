package com.technochord.ai.vacationplanner.service;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.technochord.ai.vacationplanner.config.properties.CurrencyExchangeProperties;
import com.technochord.ai.vacationplanner.model.ExchangeRates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.function.Function;

public class CurrencyExchangeService implements Function<CurrencyExchangeService.Request, CurrencyExchangeService.Response> {
    private final Logger log = LoggerFactory.getLogger(CurrencyExchangeService.class);
    private CurrencyExchangeProperties currencyExchangeProperties;

    private RestTemplate restTemplate;

    public CurrencyExchangeService(final CurrencyExchangeProperties currencyExchangeProperties,
                                   final RestTemplate restTemplate) {
        this.currencyExchangeProperties = currencyExchangeProperties;
        this.restTemplate = restTemplate;
    }

    @Override
    public Response apply(Request request) {
        log.info("Called CurrencyExchangeService with " + request);

        double out = this.getExchange(request.currencyIn.name(), request.currencyOut.name(), request.amount);
        return new Response(out, request.currencyOut);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonClassDescription("The currency specified in currencyOut after applying the appropriate exchange " +
        "rate to the amount in the currency passed in as currencyIn")
    public record Request(
        @JsonProperty(required = true,
            value = "amount") @JsonPropertyDescription("The amount of any given currency") double amount,
        @JsonProperty(required = true,
            value = "currencyIn") @JsonPropertyDescription("The currency in which amount is specified") AirfareService.Currency currencyIn,

        @JsonProperty(required = false,
            value = "currencyOut") @JsonPropertyDescription("The currency in which amount is required") AirfareService.Currency currencyOut
        )
    {
    }

    public record Response(double amount, AirfareService.Currency currencyOut)
    {
    }

    public double getExchange(final String currencyIn, final String currencyOut, final double amount) {
        AirfareService.Currency currencyEnumIn = AirfareService.Currency.valueOf(currencyIn);
        AirfareService.Currency currencyEnumOut = AirfareService.Currency.valueOf(currencyOut);
        String url = currencyExchangeProperties.getVatComply().getUrl()
                + "?base=" + currencyEnumIn.name();

        ResponseEntity<ExchangeRates> resp = restTemplate.getForEntity(url, ExchangeRates.class);
        ExchangeRates exchangeRates = resp.getBody();
        Double rate = exchangeRates.getRateMap().get(currencyEnumOut.name());

        return rate * amount;
    }
}
