package com.tripservice.dto.event;

public record TripCompletedEvent (
   Long tripId,
   Long driverId,
   Long passengerId
){}

