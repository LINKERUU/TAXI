package com.tripservice.service.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripservice.dto.event.TripCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class TripProducerEvent {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Value("${kafka.topics.trip-completed}")
  private String topic;

  public void  sendTripCompletedEvent(Long tripId, Long driverId,
                                      Long passengerId) {
    try {
      TripCompletedEvent event = TripCompletedEvent.builder()
              .tripId(tripId)
              .driverId(driverId)
              .passengerId(passengerId)
              .build();

      String key = "trip-" + tripId;
      String eventJson = objectMapper.writeValueAsString(event);

      kafkaTemplate.send(topic, key, eventJson)
              .whenComplete((result, ex) ->
              {
                if (ex == null) {
                  log.info("Trip completed event sent: tripId={}", tripId);
                } else
                  log.info("Failed to send event: tripId={}", tripId);
              });
    }
    catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to serialize event to JSON", e);
    } catch (Exception e) {
      throw new RuntimeException("Failed to send event to Kafka", e);
    }
  }

}
