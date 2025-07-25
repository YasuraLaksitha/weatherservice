package com.skycast.weatherservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationListWrapper {

    @JsonProperty("List")
    private List<LocationDetails> list;
}
