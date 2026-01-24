package com.passengerservice.controller;

import com.passengerservice.dto.RequestPassenger;
import com.passengerservice.dto.ResponcePassenger;
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
  public ResponseEntity<ResponcePassenger> createPassenger(@RequestBody RequestPassenger passenger) {
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(passengerService.createPassenger(passenger));
  }

  @GetMapping("/{id}")
  public ResponseEntity<Optional<Passenger>> getPassenger(@PathVariable Long id) {
    return ResponseEntity.ok(passengerService.getPassenger(id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Passenger> deletePassenger(@PathVariable Long id) throws Exception {
    passengerService.deletePassenger(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}")
  public ResponseEntity<ResponcePassenger> updatePassenger(@PathVariable Long id, @RequestBody RequestPassenger passenger) throws Exception {
    return ResponseEntity.ok(passengerService.updatePassenger(id,passenger));
  }

}
