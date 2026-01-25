package com.passengerservice.service;

import com.passengerservice.dto.PassengerRequest;
import com.passengerservice.dto.PassengerResponce;
import com.passengerservice.model.Passenger;

import java.util.Optional;


public interface PassengerService {

  PassengerResponce createPassenger(PassengerRequest passenger);
  void deletePassenger(Long id) throws Exception;
  PassengerResponce updatePassenger(Long id, PassengerRequest passenger) throws Exception;
  Optional<Passenger> getPassengerById(Long id);
}
