package com.tripservice.service.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripservice.dto.event.TripCompletedEvent;
import com.tripservice.model.Outbox;
import com.tripservice.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxService {

  private final ObjectMapper objectMapper;
  private final OutboxRepository outboxRepository;

  public void saveEvent(TripCompletedEvent event, String eventType) {
    try{
      String payload = objectMapper.writeValueAsString(event);


      Outbox outbox = new Outbox(
              event.tripId(),
              eventType,
              payload
      );

      outboxRepository.save(outbox);

      log.info("Outbox saved");

    }
    catch (Exception e) {
      throw new RuntimeException("Failed to serialize event", e);
    }
  }
}
