package com.skycast.weatherservice.service.impl;

import com.skycast.weatherservice.dto.*;
import com.skycast.weatherservice.repository.WeatherRepository;
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
import java.util.Optional;

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

    private final WeatherRepository repository;

    /**
     * Implementation of {@link IWeatherService} to retrieve weather data from OpenWeatherMap API
     * and map into {@link WeatherSummaryDTO} objects
     * <p>
     * Uses a list of city codes of locations from {@link LocationListWrapper} to make HTTP calls
     *
     * @return a list of WeatherSummaryDTO which contains weather data for each location
     */
    @Override
    public WeatherSummaryListDTOWrapper retrieveAllWeatherData() {
        log.info("Starting retrieval and mapping of weather data.");

        List<WeatherSummaryDTO> summaryDTOS = new ArrayList<>();

        for (LocationDetails locationDetails : locationListWrapper.getList()) {

            final String cityCode = locationDetails.getCityCode();
            final WeatherSummaryDTO dto = retrieveData(cityCode);

            if (dto != null) {
                repository.save(dto);
                summaryDTOS.add(dto);

                log.debug("Added WeatherSummaryDTO for city '{}': {}", dto.getCityName(), dto);
            } else {
                log.warn("Skipping city code {} due to retrieval failure", cityCode);
            }
        }

        return WeatherSummaryListDTOWrapper.builder()
                .weatherSummaries(summaryDTOS)
                .build();
    }

    /**
     * Maps WeatherApiResponse to WeatherSummaryDTO
     *
     * @param weatherApiResponse weatherApiResponse
     * @return WeatherSummaryDTO
     */
    private WeatherSummaryDTO mapToWeatherSummaryDTO(WeatherApiResponse weatherApiResponse) {
        final WeatherApiResponse.Sys sys = weatherApiResponse.getSys();
        final WeatherApiResponse.Main main = weatherApiResponse.getMain();
        final WeatherApiResponse.Weather weather = weatherApiResponse.getWeather().getFirst();

        return WeatherSummaryDTO.builder()
                .cityName(weatherApiResponse.getName())
                .weatherDescription(weather.getDescription())
                .temp(main.getTemp())
                .tempMin(main.getTempMin())
                .tempMax(main.getTempMax())
                .humidity(main.getHumidity())
                .pressure(main.getPressure())
                .id(weatherApiResponse.getId())
                .country(sys.getCountry())
                .icon(weather.getIcon())
                .expiration(Long.parseLong(findExpiration(weatherApiResponse.getId())) * 60)
                .sunrise(formatTime(sys.getSunrise(), weatherApiResponse.getTimezone()))
                .sunset(formatTime(sys.getSunset(), weatherApiResponse.getTimezone()))
                .visibility(weatherApiResponse.getVisibility() / 1000)
                .windSpeed(weatherApiResponse.getWind().getSpeed())
                .windDegree(weatherApiResponse.getWind().getDegree())
                .build();
    }

    /**
     * Method will filter and find the expiration time from the Json content
     *
     * @param cityCode - cityCode
     * @return - Expiration Time
     */
    private String findExpiration(final String cityCode) {
        return locationListWrapper.getList().stream()
                .filter(location -> location.getCityCode().equals(cityCode))
                .map(LocationDetails::getExpirationTime)
                .findFirst()
                .orElse(null);
    }

    /**
     * Converts epoch seconds and timezone offset to a formatted time string
     *
     * @param epochSeconds  The epoch time is seconds (UTC)
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

    /**
     * Fetches weather data according to the city code.
     * If it is cache hit, it will retrieve the WeatherSummaryDTO.
     * IF it is cache mis, it will fetch data from the weather API and convert to WeatherSummaryDTO
     *
     * @param cityCode cityCode
     * @return WeatherSummaryDTO
     */
    private WeatherSummaryDTO retrieveData(final String cityCode) {
        final Optional<WeatherSummaryDTO> weatherSummaryDTO = repository.findById(cityCode);

        if (weatherSummaryDTO.isPresent()) return weatherSummaryDTO.get();

        final String uriString = UriComponentsBuilder.fromUriString(BASE_URL)
                .queryParam("id", cityCode)
                .queryParam("units", "metric")
                .queryParam("appid", apiKey)
                .toUriString();

        try {
            final WeatherApiResponse apiResponse = restTemplate.getForObject(
                    uriString,
                    WeatherApiResponse.class
            );

            if (apiResponse == null) {
                log.warn("Failed to retrieve weather data for city code: {}", cityCode);

            } else {
                log.debug("Api Response found for city name: {}", apiResponse.getName());

                return mapToWeatherSummaryDTO(apiResponse);
            }

        } catch (RestClientException e) {
            log.error("Exception while retrieving weather data for city code: {}", cityCode, e);
        }
        return null;
    }
}
