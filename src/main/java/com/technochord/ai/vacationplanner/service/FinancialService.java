package com.technochord.ai.vacationplanner.service;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class FinancialService implements Function<FinancialService.Request, FinancialService.Response>  {

    private final Logger log = LoggerFactory.getLogger(FinancialService.class);

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonClassDescription("Service that returns my bank balance in USD, to determine which vacations I can afford.")
    public record Request(
            @JsonProperty(required = false,
                    value = "accountNumber") @JsonPropertyDescription("The account number to check the balance in") String accountNumber
    )
    {
    }

    public record Response(double bankBalance)
    {
    }

    @Override
    public FinancialService.Response apply(FinancialService.Request request) {
        log.info("Called FinancialService with " + request);
        //In a real situation, hit your bank API here or a local db.

        Response cannedResponseForNow = new Response(1500.00);
        log.info("FinancialService response: " + cannedResponseForNow);
        return cannedResponseForNow;
    }

}
