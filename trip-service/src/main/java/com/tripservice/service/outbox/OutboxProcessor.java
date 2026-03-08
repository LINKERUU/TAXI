package com.tripservice.service.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripservice.dto.event.TripCompletedEvent;
import com.tripservice.model.Outbox;
import com.tripservice.repository.OutboxRepository;
import com.tripservice.service.producer.TripEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class OutboxProcessor {

  private final OutboxRepository outboxRepository;
  private final ObjectMapper objectMapper;
  private final TripEventProducer producer;

  private static final String completedEvent = "trip-completed-event";

  @Scheduled(fixedDelay = 2000)
  @Transactional
  public void process() {

    List<Outbox> events = outboxRepository.findTop100ByProcessedFalseOrderByCreatedAtAsc();

    for (Outbox event : events) {
      try {

        if (completedEvent.equals(event.getEventType())) {

          TripCompletedEvent payload =
                  objectMapper.readValue(event.getPayload(), TripCompletedEvent.class);

          log.info("Event is {}", payload);
          producer.publish(payload);
          log.info("Published trip-completed-event");
        }

        event.markProcessed();
        outboxRepository.save(event);
      } catch (Exception e) {
        log.error("Failed processing outbox id={}", event.getId(), e);
      }
    }
  }
}