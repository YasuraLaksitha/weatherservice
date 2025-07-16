package com.skycast.weatherservice.service.impl;

import com.skycast.weatherservice.dto.WeatherSummaryDTO;
import com.skycast.weatherservice.dto.LocationListWrapper;
import com.skycast.weatherservice.dto.WeatherApiResponse;
import com.skycast.weatherservice.service.IWeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class WeatherServiceImpl implements IWeatherService {

    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather";

    @SuppressWarnings("unused")
    @Value("${weather.api-key}")
    private String apiKey;

    private final LocationListWrapper locationListWrapper;

    private final RestTemplate restTemplate;

    /**
     * Implementation of {@link IWeatherService} to retrieve weather data from OpenWeatherMap API
     * and map into {@link WeatherSummaryDTO} objects
     * <p>
     * Uses a list of city codes of locations from {@link LocationListWrapper} to make HTTP calls
     *
     * @return a list of WeatherSummaryDTO which contains weather data for each location
     */
    @Override
    public List<WeatherSummaryDTO> retrieveAllWeatherData() {
        log.info("Starting retrieval and mapping of weather data.");

        final List<WeatherApiResponse> weatherApiResponses = requestWeatherData();

        if (weatherApiResponses.isEmpty()) {
            log.warn("No weather data received from API");
            return List.of();
        }

        final List<WeatherSummaryDTO> summaryDTOS = weatherApiResponses.stream().map(weatherApiResponse -> {
            final WeatherApiResponse.Sys sys = weatherApiResponse.getSys();
            final WeatherApiResponse.Main main = weatherApiResponse.getMain();

            final WeatherSummaryDTO summaryDTO = WeatherSummaryDTO.builder()
                    .cityName(weatherApiResponse.getName())
                    .weatherDescription(weatherApiResponse.getWeather().getFirst().getDescription())
                    .temp(main.getTemp())
                    .tempMin(main.getTempMin())
                    .tempMax(main.getTempMax())
                    .humidity(main.getHumidity())
                    .pressure(main.getPressure())
                    .sunrise(formatTime(sys.getSunrise(), weatherApiResponse.getTimezone()))
                    .sunset(formatTime(sys.getSunset(), weatherApiResponse.getTimezone()))
                    .visibility(weatherApiResponse.getVisibility() / 1000)
                    .windSpeed(weatherApiResponse.getWind().getSpeed())
                    .windDegree(weatherApiResponse.getWind().getDegree())
                    .build();

            log.debug("Mapped WeatherSummaryDTO for city '{}': {}", summaryDTO.getCityName(), summaryDTO);

            return summaryDTO;
        }).toList();

        log.info("Successfully mapped {} weather summaries", summaryDTOS.size());
        return summaryDTOS;
    }

    /**
     * Makes HTTP GET requests to the weather API for each location and collects
     * weather data to raw {@link WeatherApiResponse} objects
     *
     * @return List of raw {@link WeatherApiResponse} from the weather API.
     */
    private List<WeatherApiResponse> requestWeatherData() {
        log.info("Starting to request weather data from the API");

        final ArrayList<WeatherApiResponse> weatherApiResponses = new ArrayList<>();

        locationListWrapper.getList().forEach(locationDetails -> {
            final String uriString = UriComponentsBuilder.fromUriString(BASE_URL)
                    .queryParam("id", locationDetails.getCityCode())
                    .queryParam("units", "metric")
                    .queryParam("appid", apiKey)
                    .toUriString();

            try {
                final WeatherApiResponse apiResponse = restTemplate.getForObject(
                        uriString,
                        WeatherApiResponse.class
                );

                if (apiResponse == null) {
                    log.warn("Failed to retrieve weather data for city code: {}", locationDetails.getCityCode());
                } else {
                    log.debug("Api Response found for city name: {}", apiResponse.getName());
                    weatherApiResponses.add(apiResponse);
                }
            } catch (RestClientException e) {
                log.error("Exception while retrieving weather data for city code: {}", locationDetails.getCityCode(), e);
            }
        });

        log.info("Successfully retrieved weather data for {} city codes", weatherApiResponses.size());
        return weatherApiResponses;
    }

    /**
     *Converts epoch seconds and timezone offset to a formatted time string
     *
     * @param epochSeconds The epoch time is seconds (UTC)
     * @param offsetSeconds The timezone offset in seconds
     * @return Formatted time similar to "7.32 pm" adjusted to timezone
     */
    private String formatTime(final long epochSeconds, final int offsetSeconds) {
        final ZoneOffset offset = ZoneOffset.ofTotalSeconds(offsetSeconds);
        final ZonedDateTime zonedDateTime = Instant.ofEpochSecond(epochSeconds).atOffset(offset).toZonedDateTime();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");

        final String formatted = zonedDateTime.format(formatter);
        log.debug("Formatted time for epochSeconds: {} with offsetSeconds: {} is {}", epochSeconds, offsetSeconds, formatted);

        return formatted;
    }
}
