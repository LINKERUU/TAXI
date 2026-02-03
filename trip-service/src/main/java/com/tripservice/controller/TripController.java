package com.tripservice.controller;


import com.tripservice.dto.StatusUpdateRequest;
import com.tripservice.dto.TripRequest;
import com.tripservice.dto.TripResponse;
import com.tripservice.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {
  private final TripService tripService;

  @PostMapping
  public ResponseEntity<TripResponse> createTrip(@Valid @RequestBody TripRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(tripService.createTrip(request));
  }

  @GetMapping("/{id}")
  public ResponseEntity<TripResponse> getTrip(@PathVariable Long id) {
    return ResponseEntity.ok(tripService.getTripById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<TripResponse> updateTrip(
          @PathVariable Long id,
          @Valid @RequestBody TripRequest request) {
    return ResponseEntity.ok(tripService.updateTrip(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
    tripService.deleteTrip(id);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<TripResponse> updateTripStatus(
          @PathVariable Long id,
          @Valid @RequestBody StatusUpdateRequest request) {
    TripResponse response = tripService.updateTripStatus(id, request);
    return ResponseEntity.ok(response);
  }

}