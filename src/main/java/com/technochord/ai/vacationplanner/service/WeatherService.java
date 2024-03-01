package com.technochord.ai.vacationplanner.service;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class WeatherService implements Function<WeatherService.Request, WeatherService.Response> {

    private final Logger log = LoggerFactory.getLogger(WeatherService.class);
    /**
     * Weather Function request.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonClassDescription("Gets the current weather for a location")
    public record Request(
        @JsonProperty(required = true, value = "location") @JsonPropertyDescription("The city and state e.g. San Francisco, CA") String location,
        @JsonProperty(required = true, value = "lat") @JsonPropertyDescription("The city latitude") double lat,
        @JsonProperty(required = true, value = "lon") @JsonPropertyDescription("The city longitude") double lon,
        @JsonProperty(required = true, value = "unit") @JsonPropertyDescription("Temperature unit") Unit unit) {
    }

    /**
     * Temperature units.
     */
    public enum Unit {

        /**
         * Celsius.
         */
        C("metric"),
        /**
         * Fahrenheit.
         */
        F("imperial");

        /**
         * Human readable unit name.
         */
        public final String unitName;

        private Unit(String text) {
            this.unitName = text;
        }

    }

    /**
     * Weather Function response.
     */
    public record Response(double temp, double feels_like, double temp_min, double temp_max, int pressure, int humidity,
                           Unit unit) {
    }

    @Override
    public Response apply(Request request) {
        log.info("Called MockWeatherService with request: " + request);
        Response response = null;
        double temperature = 0;
        if (request.location().toLowerCase().indexOf("Orlando") != -1) {
            response = new Response(15, 5, 1, 30, 20, 45, Unit.C);
        }
        else if (request.location().toLowerCase().indexOf("Phoenix") != -1) {
            response = new Response(95, 105, 50, 120, 30, 85, Unit.F);
        } else if (request.location().contains("San Francisco")) {
                response = new Response(75, 75, 60, 80, 25, 40, Unit.F);
        } else if (request.location().contains("London")) {
            response = new Response(72, 72, 60, 80, 25, 40, Unit.F);
        } else if (request.location().contains("Berlin")) {
            response = new Response(78, 75, 60, 80, 25, 40, Unit.F);
        }

        return response;
    }

}
