package com.skycast.weatherservice.service;

import com.skycast.weatherservice.dto.WeatherSummaryListDTOWrapper;

public interface IWeatherService {

    /**
     * Retrieves weather data for all configured city codes from external API and maps to a List fo DTOs
     *
     * @return List<WeatherSummaryDTO> containing summarized weather data for each city code
     */
    WeatherSummaryListDTOWrapper retrieveAllWeatherData();
}
