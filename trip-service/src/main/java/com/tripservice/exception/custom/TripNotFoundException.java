package com.tripservice.exception.custom;

import com.tripservice.exception.dto.ErrorCode;

public class TripNotFoundException extends BaseException {
  public TripNotFoundException(Long id) {
    super("Trip not found with id " + id, ErrorCode.TRIP_NOT_FOUND);
  }
}
