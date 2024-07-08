package com.technochord.ai.vacationplanner.service;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.technochord.ai.vacationplanner.config.RagCandidate;
import lombok.extern.log4j.Log4j2;

import java.util.function.Function;

@Log4j2
@RagCandidate
public class RecipeService implements Function<RecipeService.Request, RecipeService.Response> {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonClassDescription("Service that returns nutrition content in various dishes based on the name and the weight of the dish.")
    public record Request(
            @JsonProperty(required = true,
                    value = "dishName") @JsonPropertyDescription("The name of the dish") String dishName,
            @JsonProperty(required = true,
                    value = "grams") @JsonPropertyDescription("The weight of the prepared dish in grams") String grams
            )
    {
    }

    public record Response(double protein, double carbs, double fat)
    {
    }

    @Override
    public RecipeService.Response apply(RecipeService.Request request) {
        log.info("Called RecipeService with " + request);
        //In a real situation, hit food APIs here.

        RecipeService.Response cannedResponseForNow = new RecipeService.Response(1.2, 0.9, 0.5);
        log.info("RecipeService response: " + cannedResponseForNow);
        return cannedResponseForNow;
    }

}
