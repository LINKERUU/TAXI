package com.ratingservice.exception.custom;


import com.ratingservice.exception.dto.ErrorCode;

public class RatingNotFoundException extends BaseException {

  public RatingNotFoundException(Long id) {
    super("Rating not found with id " + id, ErrorCode.RATING_NOT_FOUND);
  }
}
