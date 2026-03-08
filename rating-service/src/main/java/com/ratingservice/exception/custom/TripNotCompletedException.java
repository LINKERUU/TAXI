package com.ratingservice.exception.custom;

import com.ratingservice.exception.dto.ErrorCode;

public class TripNotCompletedException extends BaseException {

  public TripNotCompletedException(Long tripId) {
    super("Trip not completed with id: " + tripId, ErrorCode.TRIP_NOT_COMPLETED);
  }
}