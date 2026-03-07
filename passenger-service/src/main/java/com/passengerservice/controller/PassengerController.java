package com.passengerservice.controller;


import com.passengerservice.dto.PassengerRequest;
import com.passengerservice.dto.PassengerResponse;
import com.passengerservice.model.Passenger;
import com.passengerservice.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/api/passengers")
@RequiredArgsConstructor
public class PassengerController {

  private final PassengerService passengerService;

  @PostMapping
  public ResponseEntity<PassengerResponse> createPassenger(@RequestBody PassengerRequest passenger) {
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(passengerService.createPassenger(passenger));
  }

  @GetMapping("/{id}")
  public ResponseEntity<PassengerResponse> getPassenger(@PathVariable Long id) {
    return ResponseEntity.ok(passengerService.getPassengerById(id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Passenger> deletePassenger(@PathVariable Long id) throws Exception {
    passengerService.deletePassenger(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}")
  public ResponseEntity<PassengerResponse> updatePassenger(@PathVariable Long id, @RequestBody PassengerRequest passenger) throws Exception {
    return ResponseEntity.ok(passengerService.updatePassenger(id,passenger));
  }

}
