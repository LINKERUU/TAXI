package com.passengerservice.exception.custom;

import com.passengerservice.exception.dto.ErrorCode;

public class DuplicatePhoneException extends BaseException {

  public DuplicatePhoneException(String phone) {
    super(phone, ErrorCode.DUPLICATE_PHONE);
  }
}
