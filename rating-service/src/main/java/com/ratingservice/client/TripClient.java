package com.ratingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@FeignClient(name = "trip-service", url = "${trip.service.url}")
public interface TripClient {

  @GetMapping("/api/trips/{tripId}")
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
}