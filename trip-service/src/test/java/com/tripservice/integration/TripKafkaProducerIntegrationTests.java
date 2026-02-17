package com.tripservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tripservice.dto.event.TripCompletedEvent;
import com.tripservice.service.producer.TripProducerEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TripKafkaProducerIntegrationTests {

  @Container
  static KafkaContainer kafka = new KafkaContainer(
          DockerImageName.parse("confluentinc/cp-kafka:7.4.0")
  );

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
          .withDatabaseName("testdb")
          .withUsername("test")
          .withPassword("test");

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {

    registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    registry.add("kafka.topics.trip-completed", () -> "trip.completed.events.test");

    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

    registry.add("eureka.client.enabled", () -> "false");
    registry.add("spring.cloud.discovery.enabled", () -> "false");
    registry.add("spring.liquibase.enabled", () -> "false");

    registry.add("feign.client.enabled", () -> "false");
    registry.add("spring.cloud.openfeign.enabled", () -> "false");
  }

  @Autowired
  private TripProducerEvent tripProducerEvent;

  @Autowired
  private ObjectMapper objectMapper;

  private Consumer<String, String> consumer;
  private final String topic = "trip.completed.events.test";

  @BeforeEach
  void setUp() {
    objectMapper.registerModule(new JavaTimeModule());

    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group-" + System.currentTimeMillis() + "-" + UUID.randomUUID());
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
    props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.tripservice.dto.event");

    consumer = new DefaultKafkaConsumerFactory<String, String>(props).createConsumer();
    consumer.subscribe(Collections.singletonList(topic));

    consumer.poll(Duration.ofMillis(100));
  }

  @AfterEach
  void tearDown() {
    if (consumer != null) {
      consumer.close();
    }
  }

  @Test
  void sendTripCompletedEvent_Success() throws Exception {

    Long tripId = 1L;
    Long driverId = 100L;
    Long passengerId = 200L;

    tripProducerEvent.sendTripCompletedEvent(tripId, driverId, passengerId);

    ConsumerRecord<String, String> record =
            KafkaTestUtils.getSingleRecord(consumer, topic, Duration.ofSeconds(10));

    assertThat(record).isNotNull();
    assertThat(record.key()).isEqualTo("trip-" + tripId);

    TripCompletedEvent receivedEvent = objectMapper.readValue(
            record.value(),
            TripCompletedEvent.class
    );

    assertThat(receivedEvent.getTripId()).isEqualTo(tripId);
    assertThat(receivedEvent.getDriverId()).isEqualTo(driverId);
    assertThat(receivedEvent.getPassengerId()).isEqualTo(passengerId);
  }


  @Test
  void serializeAllFields() throws Exception {

    Long tripId = 3L;
    Long driverId = 300L;
    Long passengerId = 400L;

    tripProducerEvent.sendTripCompletedEvent(tripId, driverId, passengerId);

    ConsumerRecord<String, String> record =
            KafkaTestUtils.getSingleRecord(consumer, topic, Duration.ofSeconds(5));

    TripCompletedEvent event = objectMapper.readValue(record.value(), TripCompletedEvent.class);

    assertThat(event.getTripId()).isEqualTo(tripId);
    assertThat(event.getDriverId()).isEqualTo(driverId);
    assertThat(event.getPassengerId()).isEqualTo(passengerId);
  }

  @Test
  void handleNullValues() {
    assertThatThrownBy(() ->
            tripProducerEvent.sendTripCompletedEvent(null, 100L, 200L)
    ).isInstanceOf(Exception.class);
  }

  @Test
  void sendToCorrectTopic() throws Exception {

    Long tripId = 5L;

    tripProducerEvent.sendTripCompletedEvent(tripId, 500L, 600L);

    ConsumerRecord<String, String> record =
            KafkaTestUtils.getSingleRecord(consumer, topic, Duration.ofSeconds(5));

    assertThat(record.topic()).isEqualTo(topic);

    TripCompletedEvent event = objectMapper.readValue(record.value(), TripCompletedEvent.class);
    assertThat(event.getTripId()).isEqualTo(tripId);
  }
}