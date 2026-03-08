package com.ratingservice.exception.custom;

import com.ratingservice.exception.dto.ErrorCode;

public class ServiceUnavailableException extends BaseException {

  public ServiceUnavailableException(String message, Throwable cause) {
    super(message, ErrorCode.SERVICE_UNAVAILABLE,cause);
  }
}

