package com.skycast.weatherservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponseDTO {

    private String status;

    private String path;

    private String message;

    private LocalDateTime timestamp;
}
