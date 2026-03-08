package com.tripservice.service;

import com.tripservice.dto.StatusUpdateRequest;
import com.tripservice.dto.TripPatchRequest;
import com.tripservice.dto.TripRequest;
import com.tripservice.dto.TripResponse;

public interface TripService {
  TripResponse createTrip(TripRequest request);
  TripResponse getTripById(Long id);
  TripResponse patchTrip(Long id, TripPatchRequest request);
  void deleteTrip(Long id);
  TripResponse updateTripStatus(Long id,StatusUpdateRequest status);
}