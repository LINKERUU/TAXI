package com.tripservice.controller;


import com.tripservice.dto.StatusUpdateRequest;
import com.tripservice.dto.TripPatchRequest;
import com.tripservice.dto.TripRequest;
import com.tripservice.dto.TripResponse;
import com.tripservice.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

  private final TripService tripService;
  private static final String ID = "/{id}";

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TripResponse createTrip(@Valid @RequestBody TripRequest request) {
    return tripService.createTrip(request);
  }

  @GetMapping(ID)
  public TripResponse getTrip(@PathVariable Long id) {
    return tripService.getTripById(id);
  }

  @PatchMapping(ID)
  public TripResponse patchTrip(
          @PathVariable Long id,
          @Valid @RequestBody TripPatchRequest request) {
    return tripService.patchTrip(id, request);
  }

  @DeleteMapping(ID)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTrip(@PathVariable Long id) {
    tripService.deleteTrip(id);
  }

  @PatchMapping(ID+"/status")
  public TripResponse updateTripStatus(
          @PathVariable Long id,
          @Valid @RequestBody StatusUpdateRequest request) {
    return tripService.updateTripStatus(id, request);
  }
}