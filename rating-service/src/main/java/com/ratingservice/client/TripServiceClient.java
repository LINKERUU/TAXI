package com.ratingservice.client;

import com.ratingservice.client.dto.TripResponse;
import com.ratingservice.exception.custom.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TripServiceClient {

  private final TripClient tripClient;

  @CircuitBreaker(name = "tripService", fallbackMethod = "getTripByIdFallback")
  public TripResponse getTripById(Long tripId) {
    return tripClient.getTripById(tripId);
  }

  public TripResponse getTripByIdFallback(Long tripId, Throwable e) {

    log.error("Trip service fallback. id={}", tripId);

    throw new ServiceUnavailableException("Trip service fallback. id=" + tripId, e);
  }
}
