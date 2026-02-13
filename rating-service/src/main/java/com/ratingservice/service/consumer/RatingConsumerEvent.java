package com.ratingservice.service.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratingservice.dto.TripCompletedEvent;
import com.ratingservice.model.Rating;
import com.ratingservice.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingConsumerEvent {

  private final RatingRepository ratingRepository;
  private final ObjectMapper objectMapper;

  @KafkaListener(
          topics = "${kafka.topics.trip-completed}",
          groupId = "${spring.kafka.consumer.group-id}"
  )
  @Transactional
  public void consumeTripCompletedEvent(@Payload String message, Acknowledgment ack) {
    log.info("Received Kafka message: {}", message);

    try {
      TripCompletedEvent event = objectMapper.readValue(message, TripCompletedEvent.class);

      log.info("Deserialized event: Trip ID={}, Driver ID={}, Passenger ID={}",
              event.getTripId(), event.getDriverId(), event.getPassengerId());

      boolean driverRatingExists = ratingRepository.existsByTripIdAndRaterType(
              event.getTripId(), Rating.RaterType.DRIVER);

      boolean passengerRatingExists = ratingRepository.existsByTripIdAndRaterType(
              event.getTripId(), Rating.RaterType.PASSENGER);

      if (!driverRatingExists) {
        Rating driverRating = Rating.builder()
                .tripId(event.getTripId())
                .raterType(Rating.RaterType.DRIVER)
                .score(1)
                .comment(null)
                .build();

        ratingRepository.save(driverRating);
        log.info("Created driver rating for trip ID: {}, driver ID: {}",
                event.getTripId(), event.getDriverId());
      }
      else {
        log.info("Driver rating already exists for trip ID: {}", event.getTripId());
      }

      if (!passengerRatingExists) {
        Rating passengerRating = Rating.builder()
                .tripId(event.getTripId())
                .raterType(Rating.RaterType.PASSENGER)
                .score(1)
                .comment(null)
                .build();

        ratingRepository.save(passengerRating);
        log.info("Created passenger rating for trip ID: {}, passenger ID: {}",
                event.getTripId(), event.getPassengerId());
      } else {
        log.info("Passenger rating already exists for trip ID: {}", event.getTripId());
      }

      log.info("Rating processing completed for trip ID: {}", event.getTripId());
      ack.acknowledge();

    } catch (JsonProcessingException e) {
      log.error("Error deserializing JSON: {}", e.getMessage());
    } catch (Exception e) {
      log.error("Error processing message: {}", e.getMessage(), e);
    }
  }
}