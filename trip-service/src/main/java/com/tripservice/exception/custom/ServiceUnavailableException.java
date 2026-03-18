package com.tripservice.exception.custom;

import com.tripservice.exception.dto.ErrorCode;

public class ServiceUnavailableException extends BaseException {

  public ServiceUnavailableException(String message, Throwable cause) {
    super(message, ErrorCode.SERVICE_UNAVAILABLE, cause);
  }
}
