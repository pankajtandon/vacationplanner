package com.technochord.ai.vacationplanner.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FlightOffers {

    @JsonProperty("data")
    private List<FlightOffer> flightOffers;
    @Data
    public static class FlightOffer {
        private boolean oneWay;
        private List<Itinerary> itineraries;
        private Price price;
    }

    @Data
    public static class Itinerary {
        private String duration;
        private List<Segment> segments;
    }

    @Data
    public static class Segment {
        private Hop departure;
        private Hop arrival;
        private String duration;
    }

    @Data
    public static class Hop {
        private String iataCode;
        private String terminal;
        private Date at;
    }

    @Data
    public static class Price {
        private String currency;
        private double total;
    }
}
