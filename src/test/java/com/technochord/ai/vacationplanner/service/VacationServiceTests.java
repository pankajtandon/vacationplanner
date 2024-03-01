package com.technochord.ai.vacationplanner.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
public class VacationServiceTests {

    @Autowired
    private VacationService vacationService;

    @Test
    public void testVacationPlanning() {
        String response = vacationService.planVacation();

        Assert.notNull(response, "Response should not be null!");
    }
}
