package com.technochord.ai.vacationplanner.service;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.technochord.ai.vacationplanner.config.RagCandidate;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

@Log4j2
@RagCandidate
public class RecipeService implements Function<RecipeService.Request, RecipeService.Response> {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonClassDescription("Service that returns the percent of protein calories, carbs calories and fat calories, the total calories and the cost of the meal of a single serving of the named dish.")
    public record Request(
            @JsonProperty(required = true,
                    value = "dishName") @JsonPropertyDescription("The name of the dish") String dishName)
    {
    }

    public record Response(double proteinPercent, double carbPercent, double fatPercent, double calories, double cost)
    {
    }

    @Override
    public RecipeService.Response apply(RecipeService.Request request) {
        log.info("Called RecipeService with " + request);
        //In a real situation, hit food APIs here.

        List<List<Double>> dishInfo = List.of(List.of(25.0, 30.0, 45.0, 800.0, 23.0),
                                            List.of(30.0, 25.0, 45.0, 900.0, 21.00),
                                            List.of(32.0, 28.0, 40.0, 1000.0, 20.00),
                                            List.of(24.0, 26.0, 50.0, 750.0, 15.00),
                                            List.of(20.0, 20.0, 60.0, 1050.0, 19.00),
                                            List.of(10.0, 35.0, 55.0, 1100.0, 25.00),
                                            List.of(15.0, 25.0, 60.0, 950.0, 23.00),
                                            List.of(20.0, 35.0, 45.0, 350.0, 23.50),
                                            List.of(25.0, 40.0, 35.0, 650.0, 20.50),
                                            List.of(5.0, 25.0, 70.0, 400.0, 21.00)
                                        );
        Random random = new Random();
        int index = random.nextInt(10);

        RecipeService.Response randomizedResponseForNow = new RecipeService.Response(dishInfo.get(index).get(0),  dishInfo.get(index).get(1), dishInfo.get(index).get(2),
                dishInfo.get(index).get(3), dishInfo.get(index).get(4));
        log.info("RecipeService response: " + randomizedResponseForNow);
        return randomizedResponseForNow;
    }
}
