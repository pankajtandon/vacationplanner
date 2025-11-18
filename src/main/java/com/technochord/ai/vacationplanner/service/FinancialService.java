package com.technochord.ai.vacationplanner.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.technochord.ai.vacationplanner.config.RagCandidate;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

@RagCandidate
@Log4j2
public class FinancialService  {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Request(
            @ToolParam(required = true, description = "The account number to check the balance in") String accountNumber
    )
    {
    }

    public record Response(@JsonPropertyDescription("The current bank balance expressed in USD.") double bankBalance,
                           @JsonPropertyDescription("The monthly income expressed in USD.") double monthlyIncome)
    {
    }

    @Tool(name = "financialService", description = "Service that returns my bank balance and monthly income in USD, to determine what kind of lifestyle can I afford.")
    public FinancialService.Response apply(@ToolParam FinancialService.Request request) {
        log.info("Called FinancialService with " + request);
        //In a real situation, hit your bank API here or a local db.

        Response cannedResponseForNow = new Response(4500.00, 1000.00);
        log.info("FinancialService response: " + cannedResponseForNow);
        return cannedResponseForNow;
    }
}
