package com.passengerservice.exception.custom;

import com.passengerservice.exception.dto.ErrorCode;

public class PassengerNotFoundException extends BaseException {

  public PassengerNotFoundException(Long id) {
    super("Passenger not found with id " + id, ErrorCode.PASSENGER_NOT_FOUND);
  }
}
