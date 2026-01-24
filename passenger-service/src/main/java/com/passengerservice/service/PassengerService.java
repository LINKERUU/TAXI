package com.passengerservice.service;

import com.passengerservice.dto.RequestPassenger;
import com.passengerservice.dto.ResponcePassenger;
import com.passengerservice.model.Passenger;

import java.util.Optional;


public interface PassengerService {

  ResponcePassenger createPassenger(RequestPassenger passenger);
  void deletePassenger(Long id) throws Exception;
  ResponcePassenger updatePassenger(Long id, RequestPassenger passenger) throws Exception;
  Optional<Passenger> getPassenger(Long id);
}
