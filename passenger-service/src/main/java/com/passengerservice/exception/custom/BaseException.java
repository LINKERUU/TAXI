package com.passengerservice.exception.custom;

import com.passengerservice.exception.dto.ErrorCode;
import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {

  private final ErrorCode errorCode;

  protected BaseException(String message, ErrorCode errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

}

