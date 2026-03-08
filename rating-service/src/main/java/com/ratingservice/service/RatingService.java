package com.ratingservice.service;

import com.ratingservice.dto.RatingPatchRequest;
import com.ratingservice.dto.RatingRequest;
import com.ratingservice.dto.RatingResponse;

public interface RatingService {

  RatingResponse createRating(RatingRequest request);
  RatingResponse getRatingById(Long id);
  RatingResponse patchRating(Long id, RatingPatchRequest request);
  void deleteRating(Long id);
}