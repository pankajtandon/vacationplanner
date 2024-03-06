package com.technochord.ai.vacationplanner.service;

import com.technochord.ai.vacationplanner.model.Airports;
import com.technochord.ai.vacationplanner.model.FlightOffers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
public class AirfareServiceTests {
    @Autowired
    private AirfareService airfareService;

    @Test
    public void testGetClosestAirportPittsburgh() {
        Airports.Airport closestAirport = airfareService.getClosestAirport(40.4406, -79.9959);// Pittsburgh Lat/lng

        Assert.notNull(closestAirport, "Closest Airport should not be null!");
        Assert.isTrue(closestAirport.getIataCode().equals("PIT"), "The IATA code should be PIT");
    }

    @Test
    public void testGetClosestAirportMyrtleBeach() {
        Airports.Airport closestAirport = airfareService.getClosestAirport(33.6954, -78.8802);// MyrtleBeach Lat/lng

        Assert.notNull(closestAirport, "Closest Airport should not be null!");
        Assert.isTrue(closestAirport.getIataCode().equals("MYR"), "The IATA code should be MYR");
    }

    @Test
    public void testShopFlightsForLowestFare() {
        AirfareService.Request request = new AirfareService.Request("Pittsburgh, PA", "Myrtle Beach, SC", AirfareService.Currency.USD, 40.4406, -79.9959, 33.6954, -78.8802, "June", "2024");
        FlightOffers.FlightOffer flightOffer = airfareService.shopFlightsForLowestFare(request, "PIT", "MYR");

        Assert.notNull(flightOffer, "FlightOffer should not be null!");

        AirfareService.Response response = new AirfareService.Response(flightOffer.getPrice().getTotal(),
                AirfareService.Currency.valueOf(flightOffer.getPrice().getCurrency()));

        Assert.notNull(response, "Airfare reponse cannot be null!");
    }
}
