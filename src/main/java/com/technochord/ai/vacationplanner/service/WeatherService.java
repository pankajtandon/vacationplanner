package com.technochord.ai.vacationplanner.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.technochord.ai.vacationplanner.config.RagCandidate;
import com.technochord.ai.vacationplanner.config.properties.WeatherProperties;
import com.technochord.ai.vacationplanner.model.MonthlyWeather;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;

@RagCandidate
public class WeatherService {

    private RestTemplate restTemplate;
    private WeatherProperties weatherProperties;

    public WeatherService(final RestTemplate restTemplate, final WeatherProperties weatherProperties) {
        this.restTemplate = restTemplate;
        this.weatherProperties = weatherProperties;
    }

    private final Logger log = LoggerFactory.getLogger(WeatherService.class);
    /**
     * Weather Function request.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Request(
        @JsonProperty(required = true, value = "location") @ToolParam(description = "The city and state e.g. San Francisco, CA") String location,
        @JsonProperty(required = true, value = "lat") @ToolParam(description = "The city latitude") double lat,
        @JsonProperty(required = true, value = "lon") @ToolParam(description = "The city longitude") double lon,
        @JsonProperty(required = true, value = "unit") @ToolParam(description = "Temperature unit based on the unit used in the country in which the city is located.") Unit unit,
        @JsonProperty(required = true, value = "month") @ToolParam(description = "The month in which the weather is desired") String month,
        @JsonProperty(required = true, value = "year") @ToolParam(description = "The year in which the weather is desired") String year
        ) {
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
    public record Response(@JsonPropertyDescription("Temperature expressed in units represented by the response parameter 'unit'.") double temp,
                           @JsonPropertyDescription("Minimum temperature at this location in the specified time period.") double temp_min,
                           @JsonPropertyDescription("Maximum temperature at this location in the specified time period.") double temp_max,
                           @JsonPropertyDescription("The temerature units in which the temperature is expressed (C|F).") Unit unit) {
    }

    @Tool(name = "weatherService", description = "Gets the average weather for a location and month and year")
    public Response apply(@ToolParam Request request) {
        log.info("Called WeatherService with request: " + request);
        MonthlyWeather monthlyWeather = this.getAverageWeather(request);
        Response response = new Response(monthlyWeather.getAverageTempForMonth(), monthlyWeather.getAverageMinTempForMonth(), monthlyWeather.getAverageMaxTempForMonth(), Unit.F);
        log.info("WeatherService returned: " + response);
        return response;
    }

    public MonthlyWeather getAverageWeather(final WeatherService.Request request) {
        String url = weatherProperties.getVisualCrossing().getUrl() + "/"
                + request.lat + ","
                + request.lon + "/"
                + this.getStartDateString(request) + "/"
                + this.getEndDateString(request) + "?key="
                + weatherProperties.getVisualCrossing().getApiKey();

        ResponseEntity<MonthlyWeather> resp = restTemplate.getForEntity(url, MonthlyWeather.class);
        MonthlyWeather monthlyWeather = resp.getBody();
        return monthlyWeather;
    }

    public String getStartDateString(final WeatherService.Request request) {
        String d = "";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MMM-dd");
            Date date = simpleDateFormat.parse(request.year + "-" + request.month.substring(0, 3) + "-" + "1");
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
            d = simpleDateFormat1.format(date);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Error parsing month %s or year %s", request.month, request.year));
        }
        return d;
    }

    public String getEndDateString(final WeatherService.Request request) {
        String d = "";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MMM-dd");
            Date date = simpleDateFormat.parse(request.year + "-" + request.month.substring(0, 3) + "-" + "31");
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
            d = simpleDateFormat1.format(date);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Error parsing month %s or year %s", request.month, request.year));
        }
        return d;
    }
}
