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

    @Override
    public Response apply(Request request) {
        log.info("Called CurrencyExchangeService with " + request);
        Response response = null;
        double out = 0.0;
        Currency currency = null;

        if (request.currencyIn == Currency.POUND) {
            out = request.amount * POUND_USD;
            currency = Currency.USD;
        } else if (request.currencyIn == Currency.USD) {
            out = request.amount * USD_POUND;
            currency = Currency.POUND;
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
            value = "currencyIn") @JsonPropertyDescription("The currency in which amount is specified") Currency currencyIn,

        @JsonProperty(required = false,
            value = "currencyOut") @JsonPropertyDescription("The currency in which amount is required") Currency currencyOut
        )
    {
    }

    public record Response(double amount, Currency currencyOut)
    {
    }

    public enum Currency {
        /**
         * Dollars.
         */
        USD("US Dollar"),
        /**
         * Pounds.
         */
        POUND("British Pound");

        /**
         * Human readable currency name.
         */
        public final String currencyName;

        private Currency(String text) {
            this.currencyName = text;
        }
    }
}
