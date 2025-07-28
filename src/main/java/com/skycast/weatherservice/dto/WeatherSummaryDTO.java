package com.skycast.weatherservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("WeatherSummery")
public class WeatherSummaryDTO {

    @Id
    private String id;

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

    @TimeToLive
    private Long expiration;
}
