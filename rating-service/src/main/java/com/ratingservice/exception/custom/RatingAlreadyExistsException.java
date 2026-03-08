package com.ratingservice.exception.custom;

import com.ratingservice.exception.dto.ErrorCode;

public class RatingAlreadyExistsException extends BaseException {

  public RatingAlreadyExistsException(Long id) {
    super("Rating with this id already exists: " + id, ErrorCode.RATING_ALREADY_EXIST);
  }
}

