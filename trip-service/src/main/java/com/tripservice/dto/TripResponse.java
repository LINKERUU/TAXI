package com.tripservice.dto;

import com.tripservice.model.Address;
import com.tripservice.model.enums.TripStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;


public record TripResponse (
  Long id,
  Long driverId,
  Long passengerId,
  Address pickupAddress,
  Address destinationAddress,
  TripStatus status,
  LocalDateTime orderTime,
  BigDecimal price
){}