package com.ratingservice.dto;

public record TripCompletedEvent(
        Long tripId,
        Long driverId,
        Long passengerId
) {
}