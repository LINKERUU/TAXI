package com.tripservice.service;

import com.tripservice.dto.StatusUpdateRequest;
import com.tripservice.dto.TripRequest;
import com.tripservice.dto.TripResponse;
import jakarta.validation.Valid;

public interface TripService {
  TripResponse createTrip(TripRequest request);
  TripResponse getTripById(Long id);
  TripResponse updateTrip(Long id, TripRequest request);
  void deleteTrip(Long id);
  TripResponse updateTripStatus(Long id, @Valid StatusUpdateRequest status);
}