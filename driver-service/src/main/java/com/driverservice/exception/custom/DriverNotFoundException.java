package com.driverservice.exception.custom;

import com.driverservice.exception.dto.ErrorCode;

public class DriverNotFoundException extends BaseException {

  public DriverNotFoundException(Long id) {
    super("Driver not found with id " + id, ErrorCode.DRIVER_NOT_FOUND);
  }
}
