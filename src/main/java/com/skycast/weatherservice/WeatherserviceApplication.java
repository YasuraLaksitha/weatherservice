package com.skycast.weatherservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class WeatherserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherserviceApplication.class, args);
	}

}
