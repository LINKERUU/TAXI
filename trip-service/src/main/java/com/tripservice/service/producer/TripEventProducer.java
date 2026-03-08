package com.tripservice.service.producer;

import com.tripservice.dto.event.TripCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

//tracing pattern почитать
@RequiredArgsConstructor
@Service
@Slf4j
public class TripEventProducer {

  private static final String KEY_PREFIX = "trip-";

  private final KafkaTemplate<String, TripCompletedEvent> kafkaTemplate;

  @Value("${kafka.topics.trip-completed}")
  private String topic;

  public void publish(TripCompletedEvent event) {

    String key = KEY_PREFIX + event.tripId();

    kafkaTemplate.send(topic, key, event);
  }

}
