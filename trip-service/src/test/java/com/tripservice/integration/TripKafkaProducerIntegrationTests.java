package com.tripservice.integration;

import com.tripservice.dto.event.TripCompletedEvent;
import com.tripservice.service.producer.TripEventProducer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class TripKafkaProducerIntegrationTests extends TestConfig {

  private static final long TRIP_ID = 1;
  private static final long DRIVER_ID = 1;
  private static final long PASSENGER_ID = 1;
  private static final String TOPIC = "trip.completed.events.test";
  private static final long INVALID_ID = -1;


  @Container
  static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    registry.add("kafka.topics.trip-completed", () -> TOPIC);
  }

  @Autowired
  private TripEventProducer tripEventProducer;

  private static Consumer<String, TripCompletedEvent> consumer;

  @BeforeAll
  public static void setUp() {

    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group-" + System.currentTimeMillis() + "-" + UUID.randomUUID());
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
    props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.tripservice.dto.event");

    consumer = new DefaultKafkaConsumerFactory<String, TripCompletedEvent>(props).createConsumer();
    consumer.subscribe(Collections.singletonList(TOPIC));

    consumer.poll(Duration.ofMillis(100));
  }

  @Test
  @DisplayName("Should send event")
  public void shouldSendEvent() {

    TripCompletedEvent event = new TripCompletedEvent(TRIP_ID, DRIVER_ID, PASSENGER_ID);

    tripEventProducer.publish(event);

    ConsumerRecord<String, TripCompletedEvent> record = KafkaTestUtils.getSingleRecord(consumer, TOPIC, Duration.ofSeconds(5));

    assertThat(record.value()).isNotNull();
    assertThat(record.key()).isEqualTo("trip-" + TRIP_ID);
    assertThat(record.value().tripId()).isEqualTo(TRIP_ID);
    assertThat(record.value().driverId()).isEqualTo(DRIVER_ID);
    assertThat(record.value().passengerId()).isEqualTo(PASSENGER_ID);

  }

  @Test
  @DisplayName("Should handle event with negative IDs")
  public void shouldHandleNegativeIds() {

    TripCompletedEvent event = new TripCompletedEvent(INVALID_ID, INVALID_ID, INVALID_ID);

    tripEventProducer.publish(event);

    ConsumerRecord<String, TripCompletedEvent> record = KafkaTestUtils.getSingleRecord(consumer, TOPIC, Duration.ofSeconds(5));

    assertThat(record).isNotNull();
    assertThat(record.value()).isNotNull();
    assertThat(record.key()).isEqualTo("trip-" + INVALID_ID);
    assertThat(record.value().tripId()).isEqualTo(INVALID_ID);
    assertThat(record.value().driverId()).isEqualTo(INVALID_ID);
    assertThat(record.value().passengerId()).isEqualTo(INVALID_ID);
  }

}