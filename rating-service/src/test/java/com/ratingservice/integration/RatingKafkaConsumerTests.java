package com.ratingservice.integration;

import com.ratingservice.dto.TripCompletedEvent;
import com.ratingservice.model.enums.RaterType;
import com.ratingservice.repository.RatingRepository;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class RatingKafkaConsumerTests extends TestConfig {


  private static final long TRIP_ID = 1;
  private static final long DRIVER_ID = 10;
  private static final long PASSENGER_ID = 20;
  private static final String TOPIC = "trip.completed.events.test";


  @Container
  static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    registry.add("kafka.topics.trip-completed", () -> TOPIC);
  }

  @Autowired
  private RatingRepository ratingRepository;

  private static Producer<String, TripCompletedEvent> producer;


  @BeforeAll
  public static void setUp() {

    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

    configProps.put(ProducerConfig.ACKS_CONFIG, "all");
    configProps.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
    configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
    configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

    producer = new DefaultKafkaProducerFactory<String, TripCompletedEvent>(configProps).createProducer();

  }

  @Test
  @DisplayName("Should consume event and create ratings")
  public void shouldConsumeEventAndCreateRatings() {

    TripCompletedEvent event =
            new TripCompletedEvent(TRIP_ID, DRIVER_ID, PASSENGER_ID);

    producer.send(new ProducerRecord<>(TOPIC, "trip-" + TRIP_ID, event));
    producer.flush();

    Awaitility.await("2000");

    boolean driverExists = ratingRepository
            .existsByTripIdAndRaterType(TRIP_ID, RaterType.DRIVER);

    boolean passengerExists = ratingRepository
            .existsByTripIdAndRaterType(TRIP_ID, RaterType.PASSENGER);

    assertThat(driverExists).isTrue();
    assertThat(passengerExists).isTrue();
  }

  @Test
  @DisplayName("Should be idempotent (no duplicates)")
  public void shouldNotCreateDuplicates() {

    TripCompletedEvent event =
            new TripCompletedEvent(TRIP_ID, DRIVER_ID, PASSENGER_ID);

    producer.send(new ProducerRecord<>(TOPIC, "trip-" + TRIP_ID, event));
    producer.send(new ProducerRecord<>(TOPIC, "trip-" + TRIP_ID, event));
    producer.flush();

    Awaitility.await("2000");

    long count = ratingRepository.count();

    assertThat(count).isEqualTo(2);
  }
}
