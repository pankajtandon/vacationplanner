package com.technochord.ai.vacationplanner.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.technochord.ai.vacationplanner.config.RagCandidate;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

@RagCandidate
@Log4j2
public class BookingService {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Request(
            @ToolParam(required = true, description = "The full name of the passenger for whom the flight booking is being made.") String fullName,
            @ToolParam(required = true, description = "The email address of the passenger for whom the flight booking is being made. " +
                    "This is used to send an email confirming the booking.") String emailAddress,
            @ToolParam(required = true, description = "The start date of the flight booking") String startDate,
            @ToolParam(required = true, description = "The end date of the flight booking") String endDate,
            @ToolParam(required = true, description = "The origin location for the flight booking") String origin,
            @ToolParam(required = true, description = "The destination location for the flight booking") String destination,
            @ToolParam(required = true, description = "The airline that is being used by the user for the flight booking") String airline
    ) { }

    public record Response(@JsonPropertyDescription("A string representing the status (success or failure) of the booking.") String response)
    {
    }

    @Tool(name = "bookingService", description = "Service that makes a flight booking for any user based on the parameters passed.")
    public BookingService.Response apply(@ToolParam BookingService.Request request) {
        log.info("Called BookingService with " + request);
        //In a real situation, hit all necessary  APIs to make irreversible bookings

        log.info("BookingService: booking complete. Check your inbox!");
        return new BookingService.Response("Success!");
    }
}
