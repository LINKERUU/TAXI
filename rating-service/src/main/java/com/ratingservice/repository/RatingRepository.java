package com.ratingservice.repository;

import com.ratingservice.model.Rating;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RatingRepository extends CrudRepository<Rating, Long> {

  Optional<Rating> findByTripIdAndRaterType(Long tripId, Rating.RaterType raterType);

  boolean existsByTripIdAndRaterType(Long tripId, Rating.RaterType raterType);
}
