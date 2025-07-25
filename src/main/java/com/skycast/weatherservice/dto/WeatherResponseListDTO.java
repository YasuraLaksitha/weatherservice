package com.skycast.weatherservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WeatherResponseListDTO {

    private String status;

    private String message;

    private List<WeatherSummaryDTO> weatherApiResponseList;
}
