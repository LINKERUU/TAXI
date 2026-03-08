package com.passengerservice.exception.custom;

import com.passengerservice.exception.dto.ErrorCode;

public class DuplicateEmailException extends BaseException{

  public DuplicateEmailException(String email) {
    super(email, ErrorCode.DUPLICATE_EMAIL);
  }
}
