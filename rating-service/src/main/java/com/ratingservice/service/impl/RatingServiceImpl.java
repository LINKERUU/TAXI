package com.ratingservice.service.impl;

import com.ratingservice.client.TripClient;
import com.ratingservice.dto.RatingRequest;
import com.ratingservice.dto.RatingResponse;
import com.ratingservice.mapper.RatingMapper;
import com.ratingservice.model.Rating;
import com.ratingservice.repository.RatingRepository;
import com.ratingservice.service.RatingService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RatingServiceImpl implements RatingService {

  private final RatingRepository ratingRepository;
  private final RatingMapper ratingMapper;
  private final TripClient tripClient;

  @Override
  @Transactional
  @CircuitBreaker(name = "ratingService", fallbackMethod = "createRatingFallback")
  public RatingResponse createRating(RatingRequest request) {
    log.info("Creating rating for trip {} by {}",
            request.getTripId(), request.getRaterType());

    TripClient.TripResponse trip = getTripDetails(request.getTripId());

    validateRating(request, trip);

    checkDuplicateRating(request);

    Rating rating = ratingMapper.toEntity(request);
    Rating savedRating = ratingRepository.save(rating);

    log.info("Rating created with ID: {}", savedRating.getId());

    return ratingMapper.toResponse(savedRating);
  }

  @Override
  @CircuitBreaker(name = "ratingService", fallbackMethod = "getRatingByIdFallback")
  public RatingResponse getRatingById(Long id) {
    log.debug("Fetching rating with ID: {}", id);

    Rating rating = ratingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Rating not found with id: " + id));

    TripClient.TripResponse trip = getTripDetails(rating.getTripId());
    return ratingMapper.toResponse(rating);
  }

  @Override
  @Transactional
  public RatingResponse updateRating(Long id, RatingRequest request) {
    log.info("Updating rating with ID: {}", id);

    Rating rating = ratingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Rating not found with id: " + id));

    if (!rating.getTripId().equals(request.getTripId())) {
      throw new IllegalArgumentException("Cannot change trip ID for existing rating");
    }

    TripClient.TripResponse trip = getTripDetails(request.getTripId());
    validateRating(request, trip);

    ratingMapper.updateEntityFromRequest(request, rating);
    Rating updatedRating = ratingRepository.save(rating);

    return ratingMapper.toResponse(updatedRating);
  }

  public RatingResponse updateRatingFallback(Long id, RatingRequest request, Throwable throwable) {
    log.error("Circuit Breaker triggered for updateRating. ID: {}. Error: {}", id, throwable.getMessage());
    throw new RuntimeException(
            "Cannot update rating at the moment. Trip service is unavailable. " +
                    "Please try again later. Error: " + throwable.getMessage()
    );
  }

  @Override
  @Transactional
  public void deleteRating(Long id) {
    log.info("Deleting rating with ID: {}", id);

    if (!ratingRepository.existsById(id)) {
      throw new RuntimeException("Rating not found with id: " + id);
    }

    ratingRepository.deleteById(id);
  }

  public RatingResponse createRatingFallback(RatingRequest request, Throwable throwable) {
    log.error("Circuit Breaker triggered for createRating. Trip: {}, Rater: {}. Error: {}",
            request.getTripId(), request.getRaterType(), throwable.getMessage());

    throw new RuntimeException(
            "Cannot create rating at the moment. Trip service is unavailable. " +
                    "Please try again later. Error: " + throwable.getMessage()
    );
  }

  public RatingResponse getRatingByIdFallback(Long id, Throwable throwable) {
    log.warn("Circuit Breaker fallback for getRatingById: {}. Error: {}", id, throwable.getMessage());

    Rating rating = ratingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Rating not found with id: " + id));

    return ratingMapper.toResponse(rating);
  }

  private TripClient.TripResponse getTripDetails(Long tripId) {
    try {
      TripClient.TripResponse trip = tripClient.getTripById(tripId);
      if (trip == null) {
        throw new IllegalArgumentException("Trip with ID " + tripId + " not found");
      }
      return trip;
    } catch (Exception e) {
      log.error("Error getting trip details for ID {}: {}", tripId, e.getMessage());
      throw new IllegalArgumentException("Failed to get trip details: " + e.getMessage());
    }
  }

  private void validateRating(RatingRequest request, TripClient.TripResponse trip) {

    if (TripClient.TripStatus.COMPLETED!=trip.status()) {
      throw new IllegalArgumentException(
              "Cannot rate trip with status: " + trip.status() + ". Trip must be COMPLETED."
      );
    }

    if (request.getScore() < 1 || request.getScore() > 5) {
      throw new IllegalArgumentException("Score must be between 1 and 5");
    }
  }

  private void checkDuplicateRating(RatingRequest request) {
    ratingRepository.findByTripIdAndRaterType(request.getTripId(), request.getRaterType())
            .ifPresent(rating -> {
              throw new RuntimeException(
                      String.format("%s has already rated trip %d",
                              request.getRaterType(), request.getTripId())
          );
      });
  }

}