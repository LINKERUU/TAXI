package com.ratingservice.client.dto;


public record TripResponse(
        Long id,
        TripStatus status
) {
}