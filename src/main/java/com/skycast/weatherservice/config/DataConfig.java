package com.skycast.weatherservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skycast.weatherservice.model.LocationListWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Configuration
@Slf4j
public class DataConfig {

    @Bean
    public LocationListWrapper loadLocationWeatherData(final ObjectMapper objectMapper, final RestTemplate restTemplate) throws IOException {
        final Resource resource = new ClassPathResource("cities.json");
        final LocationListWrapper locationListWrapper = objectMapper.readValue(resource.getInputStream(), LocationListWrapper.class);

        log.debug("Location data found in DataConfig {}", locationListWrapper.getList());
        return locationListWrapper;
    }
}
