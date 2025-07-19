package com.skycast.weatherservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    public static final String CORRELATION_ID_LOG_VAR = "correlationId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            String correlationId = request.getHeader(CORRELATION_ID_HEADER);

            if (StringUtils.hasText(correlationId)) {
                log.info("Correlation ID found for request in CorrelationIdFilter: {}", correlationId);
            } else {
                correlationId = UUID.randomUUID().toString();
                log.info("Correlation ID generated for request in CorrelationIdFilter: {}", correlationId);
            }

            MDC.put(CORRELATION_ID_LOG_VAR, correlationId);
            response.addHeader(CORRELATION_ID_HEADER, correlationId);

            filterChain.doFilter(request, response);

        } finally {
            MDC.remove(CORRELATION_ID_LOG_VAR);
        }
    }
}
