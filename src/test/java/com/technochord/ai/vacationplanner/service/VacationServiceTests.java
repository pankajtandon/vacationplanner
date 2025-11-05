package com.technochord.ai.vacationplanner.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfSystemProperties;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
@EnabledIfSystemProperties( value = {
        @EnabledIfSystemProperty(named = "spring.ai.openai.apiKey", matches = ".*"),
        @EnabledIfSystemProperty(named = "weather.visualcrossing.apiKey", matches = ".*"),
        @EnabledIfSystemProperty(named = "flight.amadeus.client-id", matches = ".*"),
        @EnabledIfSystemProperty(named = "flight.amadeus.client-secret", matches = ".*")
})
public class VacationServiceTests {

//    @Autowired
//    private VacationService vacationService;

//    @Test
    public void testVacationPlanning() {
//        String message = "I live in Pittsburgh, PA and I love golf. " +
//                "In the summer of 2024, where should I fly to, in Europe or the United States, to play, where the weather " +
//                "is pleasant and it's economical too?";
//        String response = vacationService.planVacation(message, 5);
//
//        Assert.notNull(response, "Response should not be null!");
    }
}
