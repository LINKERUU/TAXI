package com.passengerservice.controller;

import com.passengerservice.dto.PassengerPatchRequest;
import com.passengerservice.dto.PassengerRequest;
import com.passengerservice.dto.PassengerResponse;
import com.passengerservice.service.PassengerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/passengers")
@RequiredArgsConstructor
public class PassengerController {

  private final PassengerService passengerService;
  private static final String ID = "/{id}";

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PassengerResponse createPassenger(@Valid @RequestBody PassengerRequest passenger) {
    return passengerService.createPassenger(passenger);
  }

  @GetMapping(ID)
  public PassengerResponse getPassenger(@PathVariable Long id) {
    return passengerService.getPassengerById(id);
  }

  @DeleteMapping(ID)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deletePassenger(@PathVariable Long id) {
    passengerService.deletePassenger(id);
  }

  @PatchMapping(ID)
  public PassengerResponse patchPassenger(
          @PathVariable Long id,
          @Valid @RequestBody PassengerPatchRequest passenger
  ) {
    return passengerService.patchPassenger(id, passenger);
  }

}
