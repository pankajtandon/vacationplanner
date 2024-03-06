package com.technochord.ai.vacationplanner.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Airports {

    @JsonProperty("data")
    private List<Airport> airportList;

    @Data
    public static class Airport {
        private String name;
        private String detailedName;
        private String iataCode;
        private Address address;
        private Distance distance;
    }

    @Data
    public static class Address {
        private String cityName;
        private String cityCode;
    }

    @Data
    public static class Distance {
        private double value;
        private String unit;
    }
}
