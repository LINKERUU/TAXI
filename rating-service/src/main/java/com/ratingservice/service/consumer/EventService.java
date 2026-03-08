package com.ratingservice.service.consumer;

import com.ratingservice.dto.TripCompletedEvent;
import com.ratingservice.model.Rating;
import com.ratingservice.model.enums.RaterType;
import com.ratingservice.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {

  private final RatingRepository ratingRepository;

  @Transactional
  public void handleTripCompletedEvent(TripCompletedEvent event, Acknowledgment ack) {
    log.info("Received TripCompletedEvent");

    createRatingIfAbsent(event.tripId(),RaterType.DRIVER,event.driverId());
    createRatingIfAbsent(event.tripId(),RaterType.PASSENGER,event.passengerId());

    ack.acknowledge();

    log.info("Finished TripCompletedEvent");
  }

  private void createRatingIfAbsent(Long tripId, RaterType type, Long entityId) {

    boolean exists = ratingRepository.existsByTripIdAndRaterType(tripId, type);

    if (exists) {
      log.info("{} rating already exists for tripId={}", type, tripId);
      return;
    }

    Rating rating = new Rating(tripId,type,1,null);

    ratingRepository.save(rating);

    log.info("Created {} rating for tripId={}, entityId={}", type, tripId, entityId);
  }
}