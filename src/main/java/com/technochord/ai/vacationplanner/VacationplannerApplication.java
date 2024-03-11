package com.technochord.ai.vacationplanner;

import com.technochord.ai.vacationplanner.service.VacationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Log4j2
@SpringBootApplication
public class VacationplannerApplication implements CommandLineRunner {

	@Autowired
	private VacationService vacationService;
	public static void main(String[] args) {
		SpringApplication.run(VacationplannerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("Arg0: " + args[0]);

		String response = vacationService.planVacation(args[0]);

		log.info("Response: {}",  response);
	}
}
