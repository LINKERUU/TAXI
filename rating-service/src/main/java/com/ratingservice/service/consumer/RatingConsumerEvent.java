package com.ratingservice.service.consumer;

import com.ratingservice.dto.TripCompletedEvent;
import com.ratingservice.model.Rating;
import com.ratingservice.model.enums.RaterType;
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

  private final EventService eventService;

  @KafkaListener(
          topics = "${kafka.topics.trip-completed}",
          groupId = "${spring.kafka.consumer.group-id}"
  )
  @Transactional
  public void consumeTripCompletedEvent(@Payload TripCompletedEvent event, Acknowledgment ack) {
    log.info("Received Kafka message: {}", event);

    eventService.handleTripCompletedEvent(event,ack);
  }
}