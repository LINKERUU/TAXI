package com.driverservice.exception.custom;

import com.driverservice.exception.dto.ErrorCode;

public class DuplicateLicensePlateException extends BaseException {

  public DuplicateLicensePlateException(String message) {
    super(message, ErrorCode.DUPLICATE_LICENSE_PLATE);
  }
}
