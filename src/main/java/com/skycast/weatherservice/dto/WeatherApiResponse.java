package com.skycast.weatherservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherApiResponse {

    private String name;

    private Main main;

    private List<Weather> weather;

    private Wind wind;

    private Sys sys;

    private Double visibility;

    private Integer timezone;

    @Data
    @NoArgsConstructor
    public static class Main {

        @JsonProperty("temp")
        private Double temp;

        @JsonProperty("temp_min")
        private Double tempMin;

        @JsonProperty("temp_max")
        private Double tempMax;

        private Double humidity;

        private Double pressure;
    }

    @Data
    @NoArgsConstructor
    public static class Weather {
        private String description;
    }

    @Data
    @NoArgsConstructor
    public static class Wind {

        private Double speed;

        @JsonProperty("deg")
        private Double degree;
    }

    @Data
    @NoArgsConstructor
    public static class Sys {

        private Long sunrise;

        private Long sunset;
    }
}
