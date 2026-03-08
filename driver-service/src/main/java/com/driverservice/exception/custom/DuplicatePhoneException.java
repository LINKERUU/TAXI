package com.driverservice.exception.custom;

import com.driverservice.exception.dto.ErrorCode;

public class DuplicatePhoneException extends BaseException {
  public DuplicatePhoneException(String message) {
    super(message,ErrorCode.DUPLICATE_PHONE);
  }
}
