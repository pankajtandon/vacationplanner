package com.technochord.ai.vacationplanner.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.technochord.ai.vacationplanner.config.RagCandidate;
import com.technochord.ai.vacationplanner.config.properties.CurrencyExchangeProperties;
import com.technochord.ai.vacationplanner.model.ExchangeRates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@RagCandidate
public class CurrencyExchangeService {
    private final Logger log = LoggerFactory.getLogger(CurrencyExchangeService.class);
    private CurrencyExchangeProperties currencyExchangeProperties;

    private RestTemplate restTemplate;

    public CurrencyExchangeService(final CurrencyExchangeProperties currencyExchangeProperties,
                                   final RestTemplate restTemplate) {
        this.currencyExchangeProperties = currencyExchangeProperties;
        this.restTemplate = restTemplate;
    }

    @Tool(name = "currencyExchangeService", description = "This service can be used to convert currencies from the input currency into the output currency by applying the current exchange rate. It can be used when money value is returned in a currency that is not the desired currency.")
    public Response apply(@ToolParam Request request) {
        log.info("Called CurrencyExchangeService with " + request);

        double out = this.getExchange(request.currencyIn.name(), request.currencyOut.name(), request.amount);
        Response response = new Response(out, request.currencyOut);
        log.info("CurrencyExchangeService response: " + response);
        return response;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Request(
         @ToolParam(required = true, description = "The amount of any given currency specified as currencyIn") double amount,
         @ToolParam(required = true, description = "The currency symbol for the input currency.") AirfareService.Currency currencyIn,
         @ToolParam(required = true, description = "The currency symbol for the output currency.") AirfareService.Currency currencyOut
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
