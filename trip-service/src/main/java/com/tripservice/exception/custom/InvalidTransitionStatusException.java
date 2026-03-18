package com.tripservice.exception.custom;

import com.tripservice.exception.dto.ErrorCode;
import com.tripservice.model.enums.TripStatus;

public class InvalidTransitionStatusException extends BaseException {
  public InvalidTransitionStatusException(TripStatus current, TripStatus next) {
    super(String.format("Cannot change trip status from %s to %s", current, next), ErrorCode.INVALID_TRANSITION_STATUS);
  }
}
