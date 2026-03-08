package com.ratingservice.service.impl;

import com.ratingservice.client.TripServiceClient;
import com.ratingservice.client.dto.TripResponse;
import com.ratingservice.client.dto.TripStatus;
import com.ratingservice.dto.RatingPatchRequest;
import com.ratingservice.dto.RatingRequest;
import com.ratingservice.dto.RatingResponse;
import com.ratingservice.exception.custom.RatingNotFoundException;
import com.ratingservice.mapper.RatingMapper;
import com.ratingservice.model.Rating;
import com.ratingservice.repository.RatingRepository;
import com.ratingservice.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

  private final RatingRepository ratingRepository;
  private final RatingMapper ratingMapper;
  private final TripServiceClient tripServiceClient;

  @Override
  @Transactional
  public RatingResponse createRating(RatingRequest request) {

    TripResponse trip = tripServiceClient.getTripById(request.getTripId());

    validateTripForRating(trip);

    Rating rating = ratingMapper.toEntity(request);
    ratingRepository.save(rating);

    log.info("Rating created with ID: {}", rating.getId());

    return ratingMapper.toResponse(rating);
  }

  @Override
  public RatingResponse getRatingById(Long id) {
    log.info("Getting rating with ID: {}", id);
    return ratingMapper.toResponse(getExistsRating(id));
  }

  @Override
  @Transactional
  public RatingResponse patchRating(Long id, RatingPatchRequest request) {

    Rating rating = getExistsRating(id);

    applyPatch(rating,request);

    log.info("Rating with ID {} updated", id);

    return ratingMapper.toResponse(rating);
  }

  @Override
  @Transactional
  public void deleteRating(Long id) {
    log.info("Deleting rating with ID: {}", id);

    getExistsRating(id);

    ratingRepository.deleteById(id);
  }

  private Rating getExistsRating(Long id) {
    return ratingRepository.findById(id)
            .orElseThrow(() -> new RatingNotFoundException(id));
  }


  private void applyPatch(Rating rating, RatingPatchRequest request) {
    Optional.ofNullable(request.getScore()).ifPresent(rating::changeScore);
    Optional.ofNullable(request.getComment()).ifPresent(rating::changeComment);
  }

  private void validateTripForRating(TripResponse trip) {

    if (trip.status() != TripStatus.COMPLETED) {
      throw new IllegalStateException(
              "Rating can only be created for completed trips. Trip id=" + trip.id()
      );
    }

    if (ratingRepository.existsByTripId(trip.id())) {
      throw new IllegalStateException(
              "Rating already exists for trip id=" + trip.id()
      );
    }
  }

}