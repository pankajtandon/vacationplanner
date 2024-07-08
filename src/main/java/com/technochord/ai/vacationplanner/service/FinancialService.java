package com.technochord.ai.vacationplanner.service;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.technochord.ai.vacationplanner.config.RagCandidate;
import lombok.extern.log4j.Log4j2;

import java.util.function.Function;

@RagCandidate
@Log4j2
public class FinancialService implements Function<FinancialService.Request, FinancialService.Response>  {

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
