package com.ratingservice.client.dto;


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