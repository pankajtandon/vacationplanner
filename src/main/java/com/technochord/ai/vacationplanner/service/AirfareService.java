package com.technochord.ai.vacationplanner.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.technochord.ai.vacationplanner.config.RagCandidate;
import com.technochord.ai.vacationplanner.config.properties.FlightProperties;
import com.technochord.ai.vacationplanner.model.Airports;
import com.technochord.ai.vacationplanner.model.FlightOffers;
import com.technochord.ai.vacationplanner.model.TokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

@RagCandidate
public class AirfareService {

    public static String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";

    private FlightProperties flightProperties;
    private RestTemplate restTemplate;

    public AirfareService (final FlightProperties flightProperties, final RestTemplate restTemplate) {
        this.flightProperties = flightProperties;
        this.restTemplate = restTemplate;

    }

    private final Logger log = LoggerFactory.getLogger(AirfareService.class);

    @Tool(name = "airfareService", description = "Service that computes airfare starting from the origin to the given destination")
    public Response getAirfare(@ToolParam Request request) {
        log.info("Called AirfareService with " + request);

        Airports.Airport closestOriginAirport = this.getClosestAirport(request.originLatitude, request.originLongitude);
        Airports.Airport closestDestinationAirport = this.getClosestAirport(request.destinationLatitude, request.destinationLongitude);

        FlightOffers.FlightOffer lowestFlightOffer = this.shopFlightsForLowestFare(request,
                closestOriginAirport.getIataCode(),
                closestDestinationAirport.getIataCode());
        Response response =  new Response(lowestFlightOffer.getPrice().getTotal(),
                Currency.valueOf(lowestFlightOffer.getPrice().getCurrency()));
        log.info("AirfareService response: " + response);
        return response;
    }

    public enum Currency {
        USD("US Dollar"),
        GBP("Great Britain Pound"),
        EUR("Euro"),
        INR("Indian Rupee"),
        JPY("Japanese Yen"),
        CAD("Canadian Dollar");

        /**
         * Human readable currency name.
         */
        public final String currencyName;

        private Currency(String text) {
            this.currencyName = text;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Request(
        @ToolParam(description = "The city and state e.g. Pittsburgh, PA where we are flying from", required = true) String origin,
        @ToolParam(description = "The city and state e.g. San Francisco, CA where we are flying to", required = true) String destination,
        @ToolParam(description = "The currency based on the country where the origin is located.", required = true) Currency currency,
        @ToolParam(description = "The latitude of the origin.", required = true) double originLatitude,
        @ToolParam(description = "The longitude of the origin.", required = true) double originLongitude,
        @ToolParam(description = "The latitude of the destination.", required = true) double destinationLatitude,
        @ToolParam(description = "The longitude of the destination.", required = true) double destinationLongitude,
        @ToolParam(description = "The month in which the travel is desired", required = true) String month,
        @ToolParam(description = "The year in which the travel is desired", required = true) String year
        )
    {
    }

    public record Response(@JsonPropertyDescription("The airfare for the trip.") double airfare,
                           @JsonPropertyDescription("The currency in which the airfare is represented.")  Currency currency)
    {
    }

    public Airports.Airport getClosestAirport(final double latitude, final double longitude) {
        Airports.Airport closestAirport = null;

        String url = flightProperties.getAmadeus().getUrlReferenceAirports()
                + "?latitude=" + latitude
                + "&longitude=" + longitude;
        ResponseEntity<Airports> resp = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(this.buildAuthHeader()), Airports.class);
        if (resp != null && resp.getBody() != null && resp.getBody().getAirportList() != null) {
            if (resp.getBody().getAirportList().size() > 0) {
                closestAirport = resp.getBody().getAirportList().get(0);
            }
        }
        return closestAirport;
    }

    public FlightOffers.FlightOffer shopFlightsForLowestFare(final Request request,
                               final String originAirportIATACode,
                               final String destinationAirportIATACode) {
        FlightOffers.FlightOffer lowestFlightOffer = null;
        String url = flightProperties.getAmadeus().getUrlShopping()
                + "?originLocationCode=" + originAirportIATACode
                + "&destinationLocationCode=" + destinationAirportIATACode
                //approx as middle of month till we are able to iterte over a date range and get the lowest fare
                + "&departureDate=" + this.getMiddleOfMonthDateString(request)
                + "&adults=1"
                + "&max=5";
        ResponseEntity<FlightOffers> resp = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(this.buildAuthHeader()), FlightOffers.class);
        if (resp != null && resp.getBody() != null && resp.getBody().getFlightOffers() != null) {
            lowestFlightOffer  = resp.getBody().getFlightOffers().stream()
                    .filter(fo -> fo.getPrice() != null)
                    .min(Comparator.comparing(fo -> fo.getPrice().getTotal())
                    )
                    .get();
        }
        return lowestFlightOffer;
    }

    private String getBearerToken() {
        String url = flightProperties.getAmadeus().getUrlSecurity();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        MultiValueMap<String, String> bodyPair = new LinkedMultiValueMap();
        bodyPair.add("grant_type", AirfareService.GRANT_TYPE_CLIENT_CREDENTIALS);
        bodyPair.add("client_id", flightProperties.getAmadeus().getClientId());
        bodyPair.add("client_secret", flightProperties.getAmadeus().getClientSecret());
        try {
            ResponseEntity<TokenResponse> response = restTemplate.exchange( url,
                    HttpMethod.POST, new HttpEntity<>(bodyPair, httpHeaders), TokenResponse.class);

            TokenResponse tokenResponse = response.getBody();
            return tokenResponse.getAccessToken();
        } catch (Exception e) {
            throw new RuntimeException("Error getting access token", e);
        }
    }

    private HttpHeaders buildAuthHeader() {
        String bearer = this.getBearerToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearer);

        return headers;
    }


    private String getMiddleOfMonthDateString(final AirfareService.Request request) {
        String d = "";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MMM-dd");
            Date date = simpleDateFormat.parse(request.year + "-" + request.month.substring(0, 3) + "-" + "15");
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
            d = simpleDateFormat1.format(date);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Error parsing month %s or year %s", request.month, request.year));
        }
        return d;
    }

}
