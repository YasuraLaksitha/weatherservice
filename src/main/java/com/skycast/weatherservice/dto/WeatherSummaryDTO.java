package com.skycast.weatherservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeatherSummaryDTO {

    private String cityName;

    private String weatherDescription;

    private Double temp;

    private Double tempMax;

    private Double tempMin;

    private Double humidity;

    private Double pressure;

    private Double windSpeed;

    private Double windDegree;

    private String sunrise;

    private String sunset;

    private Double visibility;

}
