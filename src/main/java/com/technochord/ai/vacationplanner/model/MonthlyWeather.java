package com.technochord.ai.vacationplanner.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class MonthlyWeather {
    private String address;
    private String resolvedAddress;

    private List<Daily> days;

    @Data
    public static class Daily {
        private Date datetime;
        private double temp;
        private double tempmax;
        private double tempmin;
    }

    public double getAverageTempForMonth() {

        OptionalDouble averageOptional = days.stream().mapToDouble(d -> {
            return d.temp;
        }).average();
        return averageOptional.getAsDouble();
    }

    public double getAverageMaxTempForMonth() {

        OptionalDouble averageOptional = days.stream().mapToDouble(d -> {
            return d.tempmax;
        }).average();
        return averageOptional.getAsDouble();
    }

    public double getAverageMinTempForMonth() {

        OptionalDouble averageOptional = days.stream().mapToDouble(d -> {
            return d.tempmin;
        }).average();
        return averageOptional.getAsDouble();
    }
}
