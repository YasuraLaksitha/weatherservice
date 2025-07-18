package com.skycast.weatherservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeatherSummaryDTO {

    private String cityName;

    private String country;

    private String icon;

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
