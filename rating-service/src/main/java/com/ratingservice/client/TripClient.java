package com.ratingservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@FeignClient(name = "trip-service",
        fallback = TripClient.TripClientFallback.class
)
public interface TripClient {

  @GetMapping("/api/trips/{tripId}")
  @CircuitBreaker(name="tripService", fallbackMethod =  "getDriverByIdFallback")
  TripResponse getTripById(@PathVariable("tripId") Long tripId);

  record TripResponse(
           Long id,
           Long driverId,
           Long passengerId,
           String pickupAddress,
           String destinationAddress,
           TripStatus status,
           LocalDateTime orderTime,
           BigDecimal price) {}

  enum TripStatus {
    CREATED,
    ACCEPTED,
    DRIVER_EN_ROUTE,
    PASSENGER_ON_BOARD,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;
  }

  class TripClientFallback implements TripClient {

    @Override
    public TripResponse getTripById(Long tripId) {
      return new TripResponse(
              tripId,
              -1L,
              -1L,
              "Service Unavailable",
              "Service Unavailable",
              TripStatus.CANCELLED,
              LocalDateTime.now(),
              BigDecimal.ZERO
      );
    }
  }

}