package com.passengerservice.service;

import com.passengerservice.dto.PassengerRequest;
import com.passengerservice.dto.PassengerResponse;
import com.passengerservice.model.Passenger;

import java.util.Optional;


public interface PassengerService {

  PassengerResponse createPassenger(PassengerRequest passenger);
  void deletePassenger(Long id) throws Exception;
  PassengerResponse updatePassenger(Long id, PassengerRequest passenger) throws Exception;
  PassengerResponse getPassengerById(Long id);
}
