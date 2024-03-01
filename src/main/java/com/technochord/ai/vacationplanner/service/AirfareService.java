package com.technochord.ai.vacationplanner.service;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class AirfareService implements Function<AirfareService.Request, AirfareService.Response> {

    private final Logger log = LoggerFactory.getLogger(AirfareService.class);
    @Override
    public Response apply(Request request) {
        log.info("Called AirfareService with " + request);
        Response response = null;
        if(StringUtils.startsWithIgnoreCase(request.destination, "Orlando")) {
            response = new Response(275, Currency.USD);
        } else if (StringUtils.startsWithIgnoreCase(request.destination,  "Phoenix")) {
            response = new Response(225, Currency.USD);
        } else if (StringUtils.startsWithIgnoreCase(request.destination,  "San Francisco")) {
            response = new Response(300, Currency.USD);
        } else if (StringUtils.startsWithIgnoreCase(request.destination,  "Chicago")) {
            response = new Response(175, Currency.USD);
        } else if (StringUtils.startsWithIgnoreCase(request.destination,  "Atlanta")) {
            response = new Response(160, Currency.USD);
        } else if (StringUtils.startsWithIgnoreCase(request.destination,  "Dallas")) {
            response = new Response(180, Currency.USD);
        } else if (StringUtils.startsWithIgnoreCase(request.destination,  "London")) {
            response = new Response(180, Currency.POUND);
        } else if (StringUtils.startsWithIgnoreCase(request.destination,  "Berlin")) {
            response = new Response(190, Currency.POUND);
        } else {
            response = new Response(200, Currency.USD);
        }

        return response;
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonClassDescription("Airfare starting from Pittsburgh, PA to the given destination")
    public record Request(
        @JsonProperty(required = true,
            value = "destination") @JsonPropertyDescription("The city and state e.g. San Francisco, CA where we are flying to") String destination,
        @JsonProperty(required = true,
            value = "currency") @JsonPropertyDescription("The currency in which fare is desired") Currency currency)
    {
    }

    public record Response(double airfare, Currency currency)
    {
    }
}
