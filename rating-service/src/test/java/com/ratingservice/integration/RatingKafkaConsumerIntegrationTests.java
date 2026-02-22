package com.ratingservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratingservice.dto.TripCompletedEvent;
import com.ratingservice.model.Rating;
import com.ratingservice.repository.RatingRepository;
import com.ratingservice.service.consumer.RatingConsumerEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
public class RatingKafkaConsumerIntegrationTests {

  @Container
  static KafkaContainer kafka = new KafkaContainer(
          DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
          .withDatabaseName("testdb")
          .withUsername("test")
          .withPassword("test");

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {

    registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    registry.add("kafka.topics.trip-completed", () -> "trip.completed.events.test");
    registry.add("spring.kafka.consumer.group-id",()->"test-group-"+System.currentTimeMillis());

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
  private RatingRepository ratingRepository;

  @Autowired
  private RatingConsumerEvent ratingConsumerEvent;

  @Autowired
  private ObjectMapper objectMapper;

  private Producer<String, String> producer;
  private Consumer<String, String> consumer;
  private final String topic = "trip.completed.events.test";

  @BeforeEach
  void setUp() {
    ratingRepository.deleteAll();

    Map<String, Object> producerProps = new HashMap<>();
    producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
    producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

    producer = new KafkaProducer<>(producerProps);

    Map<String, Object> consumerProps = new HashMap<>();
    consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
    consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG,"test-group-" + UUID.randomUUID().toString());

    consumer = new KafkaConsumer<>(consumerProps);
    consumer.subscribe(Collections.singletonList(topic));

  }

  @AfterEach
  void tearDown() {
    if (producer != null) {
      producer.close();
    }
    if (consumer != null) {
      consumer.close();
    }
  }

  @Test
  void shouldCreateRatingsWhenTripCompletedEventReceived() throws Exception {

    Long tripId = 1L;
    Long driverId = 100L;
    Long passengerId = 200L;

    TripCompletedEvent event = new TripCompletedEvent(tripId, driverId, passengerId);
    String eventJson = objectMapper.writeValueAsString(event);
    String key = "trip-" + tripId;

    ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, eventJson);
    producer.send(record).get(5, TimeUnit.SECONDS);


    await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {

      boolean driverRatingExists = ratingRepository.existsByTripIdAndRaterType(
              tripId, Rating.RaterType.DRIVER);
      boolean passengerRatingExists = ratingRepository.existsByTripIdAndRaterType(
              tripId, Rating.RaterType.PASSENGER);

      assertThat(driverRatingExists).isTrue();
      assertThat(passengerRatingExists).isTrue();

      Rating driverRating = ratingRepository.findByTripIdAndRaterType(
              tripId, Rating.RaterType.DRIVER).orElse(null);
      Rating passengerRating = ratingRepository.findByTripIdAndRaterType(
              tripId, Rating.RaterType.PASSENGER).orElse(null);

      assertThat(driverRating).isNotNull();
      assertThat(driverRating.getScore()).isEqualTo(1);
      assertThat(driverRating.getComment()).isNull();

      assertThat(passengerRating).isNotNull();
      assertThat(passengerRating.getScore()).isEqualTo(1);
      assertThat(passengerRating.getComment()).isNull();
    });
  }

  @Test
  void shouldNotCreateRatingsWhenTripCompletedEventReceived() throws Exception {

    Long tripId = 1L;
    Long driverId = 100L;
    Long passengerId = 200L;

    TripCompletedEvent event = new TripCompletedEvent(tripId, driverId, passengerId);
    String eventJson = objectMapper.writeValueAsString(event);
    String key = "trip-" + tripId;

    ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, eventJson);
    producer.send(record).get(5, TimeUnit.SECONDS);

    await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
      assertThat(ratingRepository.existsByTripIdAndRaterType(tripId, Rating.RaterType.DRIVER));
    });

    ProducerRecord<String, String> record2 = new ProducerRecord<>(topic, key, eventJson);
    producer.send(record).get(5, TimeUnit.SECONDS);

    await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
      Optional<Rating> driverRatingsCount = ratingRepository.findByTripIdAndRaterType(
              tripId, Rating.RaterType.DRIVER);
      Optional<Rating> passengerRatingsCount = ratingRepository.findByTripIdAndRaterType(
              tripId, Rating.RaterType.PASSENGER);

      assertThat(driverRatingsCount).isPresent();
      assertThat(passengerRatingsCount).isPresent();
    });

  }

  @Test
  void shouldHandleInvalidJsonMessage() throws Exception {

    String invalidJson = "{invalid json}";
    String key = "trip-" + 0L;

    ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, invalidJson);
    producer.send(record).get(5, TimeUnit.SECONDS);

    Thread.sleep(500);

    assertThat(ratingRepository.count()).isEqualTo(0);
  }

  @Test
  void shouldProcessMessageWithManualAck() throws Exception {

    Long tripId = 1L;
    Long driverId = 100L;
    Long passengerId = 200L;

    TripCompletedEvent event = new TripCompletedEvent(tripId, driverId, passengerId);
    String eventJson = objectMapper.writeValueAsString(event);
    String key = "trip-" + tripId;

    ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, eventJson);
    producer.send(record).get(5, TimeUnit.SECONDS);

    await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {

      boolean driverRatingExists = ratingRepository.existsByTripIdAndRaterType(
              tripId, Rating.RaterType.DRIVER);
      assertThat(driverRatingExists).isTrue();

      Map<String, Object> newConsumerProps = new HashMap<>();
      newConsumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
      newConsumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "new-group-" + UUID.randomUUID());
      newConsumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
      newConsumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
      newConsumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

      try {

        consumer.subscribe(Collections.singletonList(topic));
        var records = consumer.poll(Duration.ofSeconds(5));

        assertThat(records.count()).isEqualTo(0);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    });
  }
}
