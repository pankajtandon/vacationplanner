package com.technochord.ai.vacationplanner.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class ExchangeRates {
    private String base;

    @JsonProperty("rates")
    private Map<String, Double> rateMap;
}
