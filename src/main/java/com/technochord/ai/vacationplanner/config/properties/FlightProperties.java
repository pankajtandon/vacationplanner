package com.technochord.ai.vacationplanner.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "flight")
public class FlightProperties {

    private Amadeus amadeus;

    @Data
    public static class Amadeus {
        private String urlRoot;
        private String urlReferenceAirports;
        private String urlShopping;
        private String urlSecurity;
        private String clientId;
        private String clientSecret;
    }
}
