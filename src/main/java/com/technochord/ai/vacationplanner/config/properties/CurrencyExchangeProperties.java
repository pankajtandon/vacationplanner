package com.technochord.ai.vacationplanner.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "currency-exchange")
public class CurrencyExchangeProperties {

    private VatComply vatComply;
    @Data
    public static class VatComply {
        private String url;
    }
}
