package com.tripservice.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "outbox_event")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Outbox {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long aggregateId;
  private String eventType;

  private String payload;

  @CreationTimestamp
  private LocalDateTime createdAt;

  private boolean processed;

  public Outbox(Long aggregateId, String eventType, String payload) {
    this.aggregateId = aggregateId;
    this.eventType = eventType;
    this.payload = payload;
    this.createdAt = LocalDateTime.now();
    this.processed = false;
  }

  public void markProcessed() {
    this.processed = true;
  }
}
