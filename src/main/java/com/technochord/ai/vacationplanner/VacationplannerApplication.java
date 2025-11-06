package com.technochord.ai.vacationplanner;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Log4j2
@SpringBootApplication
public class VacationplannerApplication {

	public static void main(String[] args) {
		SpringApplication.run(VacationplannerApplication.class, args);
	}

}
