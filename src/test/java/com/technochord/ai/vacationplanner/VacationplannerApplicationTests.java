package com.technochord.ai.vacationplanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperties;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(args = "What is the capital of USA?")
@EnabledIfSystemProperties( value = {
		@EnabledIfSystemProperty(named = "spring.ai.openai.apiKey", matches = ".*"),
		@EnabledIfSystemProperty(named = "spring.ai.mistralai.apiKey", matches = ".*"),
		@EnabledIfSystemProperty(named = "weather.visualcrossing.apiKey", matches = ".*"),
		@EnabledIfSystemProperty(named = "flight.amadeus.client-id", matches = ".*"),
		@EnabledIfSystemProperty(named = "flight.amadeus.client-secret", matches = ".*")
})
class VacationplannerApplicationTests {

	@Test
	void contextLoads() {
	}

}
