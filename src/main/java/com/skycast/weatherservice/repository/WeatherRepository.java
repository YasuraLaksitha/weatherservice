package com.skycast.weatherservice.repository;

import com.skycast.weatherservice.dto.WeatherSummaryDTO;
import org.springframework.data.repository.CrudRepository;

public interface WeatherRepository extends CrudRepository<WeatherSummaryDTO,String> {
}
