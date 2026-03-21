package com.ratingservice.repository;

import com.ratingservice.model.Rating;
import com.ratingservice.model.enums.RaterType;
import org.springframework.data.repository.CrudRepository;


public interface RatingRepository extends CrudRepository<Rating, Long> {
  boolean existsByTripIdAndRaterType(Long tripId, RaterType raterType);
  boolean existsByTripId(Long id);
}
