package com.skycast.weatherservice.controller;

import com.skycast.weatherservice.constants.ApplicationDefaultConstants;
import com.skycast.weatherservice.dto.WeatherResponseListDTO;
import com.skycast.weatherservice.dto.WeatherSummaryDTO;
import com.skycast.weatherservice.service.IWeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/weather")
@Slf4j
public class WeatherController {

    private final IWeatherService weatherService;

    @GetMapping("/user/fetch-all")
    @PreAuthorize("hasAuthority('read:weatherData')")
    public ResponseEntity<WeatherResponseListDTO> fetchAll() {
        log.info("Request received for fetch all weather data");

        final List<WeatherSummaryDTO> weatherApiResponses = weatherService.retrieveAllWeatherData().getWeatherSummaries();
        final WeatherResponseListDTO weatherResponseListDTO = WeatherResponseListDTO.builder()
                .status(ApplicationDefaultConstants.RESPONSE_STATUS_200)
                .message(ApplicationDefaultConstants.RESPONSE_MESSAGE_200)
                .weatherApiResponseList(weatherApiResponses)
                .build();

        return ResponseEntity.ok().body(weatherResponseListDTO);
    }
}
