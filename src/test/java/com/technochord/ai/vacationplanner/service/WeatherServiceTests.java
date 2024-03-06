package com.technochord.ai.vacationplanner.service;

import com.technochord.ai.vacationplanner.model.MonthlyWeather;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
public class WeatherServiceTests {

    @Autowired
    private WeatherService weatherService;
    @Test
    public void testStartDate() {
        WeatherService.Request request = new WeatherService.Request("Pittsburgh", 1.0, 2.0, WeatherService.Unit.C, "July", "2024");
        String startString = weatherService.getStartDateString(request);

        Assert.isTrue(startString.equals("2024-07-01"), "Start string is incorrect");
    }

    @Test
    public void testEndDate() {
        WeatherService.Request request = new WeatherService.Request("Pittsburgh", 1.0, 2.0, WeatherService.Unit.C, "July", "2024");
        String startString = weatherService.getEndDateString(request);

        Assert.isTrue(startString.equals("2024-07-31"), "Start string is incorrect");
    }

    //2025 - 70.25
    //2024 - 70.25
    //2023 - 73.28
    @Test
    public void testGetWeather() {
        WeatherService.Request request = new WeatherService.Request("somewhere", 30.5834,-80.0878, WeatherService.Unit.C, "July", "2024");
        MonthlyWeather monthlyWeather = weatherService.getAverageWeather(request);

        Assert.notNull(monthlyWeather, "Should not be null");
    }
}
