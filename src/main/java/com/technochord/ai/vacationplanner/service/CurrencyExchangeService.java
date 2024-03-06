package com.technochord.ai.vacationplanner.service;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class CurrencyExchangeService implements Function<CurrencyExchangeService.Request, CurrencyExchangeService.Response> {
    private final Logger log = LoggerFactory.getLogger(CurrencyExchangeService.class);

    private static double USD_POUND = 0.9;

    private static double POUND_USD = 1.1;

    private static double POUND_EUR = 1.03;

    private static double EUR_POUND = 0.97;

    private static double USD_EUR = 0.96;

    private static double EUR_USD = 1.01;

    @Override
    public Response apply(Request request) {
        log.info("Called CurrencyExchangeService with " + request);
        Response response = null;
        double out = 0.0;
        AirfareService.Currency currency = null;

        if (request.currencyIn == AirfareService.Currency.POUND) {
            if (request.currencyOut == AirfareService.Currency.USD) {
                out = request.amount * POUND_USD;
                currency = AirfareService.Currency.USD;
            } else if (request.currencyOut == AirfareService.Currency.EUR) {
                out = request.amount * POUND_EUR;
                currency = AirfareService.Currency.EUR;
            }
        } else if (request.currencyIn == AirfareService.Currency.USD) {
            if (request.currencyOut == AirfareService.Currency.POUND) {
                out = request.amount * USD_POUND;
                currency = AirfareService.Currency.POUND;
            } else if (request.currencyOut == AirfareService.Currency.EUR) {
                out = request.amount * USD_EUR;
                currency = AirfareService.Currency.EUR;
            }
        } else if (request.currencyIn == AirfareService.Currency.EUR) {
            if (request.currencyOut == AirfareService.Currency.POUND) {
                out = request.amount * EUR_POUND;
                currency = AirfareService.Currency.POUND;
            } else if (request.currencyOut == AirfareService.Currency.USD) {
                out = request.amount * EUR_USD;
                currency = AirfareService.Currency.USD;
            }
        } else {
            throw new UnsupportedOperationException("Invalid currency specified in request: " + request);
        }
        return new Response(out, currency);
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
}
