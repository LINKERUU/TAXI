package com.driverservice.exception.custom;

import com.driverservice.exception.dto.ErrorCode;

public class DuplicateEmailException extends BaseException{

  public DuplicateEmailException(String message) {
    super(message, ErrorCode.DUPLICATE_EMAIL);
  }
}
