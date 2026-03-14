package com.passengerservice.service;

import com.passengerservice.dto.PassengerPatchRequest;
import com.passengerservice.dto.PassengerRequest;
import com.passengerservice.dto.PassengerResponse;
import com.passengerservice.model.Passenger;

public interface PassengerService {

  PassengerResponse createPassenger(PassengerRequest passenger);

  void deletePassenger(Long id);

  PassengerResponse patchPassenger(Long id, PassengerPatchRequest passenger);

  PassengerResponse getPassengerById(Long id);

  Passenger getExistsPassenger(Long id);
}
