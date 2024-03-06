package com.technochord.ai.vacationplanner.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "weather")
public class WeatherProperties {

    private VisualCrossing visualCrossing;

    @Data
    public static class VisualCrossing {
        private String apiKey;
        private String url;
    }
}
