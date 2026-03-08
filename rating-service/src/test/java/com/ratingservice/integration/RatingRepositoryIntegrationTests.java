package com.ratingservice.integration;

import com.ratingservice.model.Rating;
import com.ratingservice.repository.RatingRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RatingRepositoryIntegrationTests {

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
          .withDatabaseName("test")
          .withUsername("root")
          .withPassword("toot");

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.liquibase.enabled", () -> "false");
  }

  @Autowired
  RatingRepository ratingRepository;

  @Autowired
  private EntityManager entityManager;

  private Rating rating;

  @BeforeEach
  void setUp() {

    ratingRepository.deleteAll();

    rating = Rating.builder()
            .tripId(1L)
            .raterType(Rating.RaterType.DRIVER)
            .score(1)
            .build();
  }

  @Test
  void saveAndFindById() {
    Rating saved = ratingRepository.save(rating);
    Optional<Rating> found = ratingRepository.findById(saved.getId());

    assertThat(found.isPresent()).isTrue();
    assertThat(found.get().getTripId()).isEqualTo(rating.getTripId());
    assertThat(found.get().getRaterType()).isEqualTo(rating.getRaterType());
    assertThat(found.get().getScore()).isEqualTo(rating.getScore());
  }


  @Test
  void deleteByIdRating() {
    Rating saved = ratingRepository.save(rating);
    ratingRepository.delete(saved);

    Optional<Rating> found = ratingRepository.findById(saved.getId());
    assertThat(found.isPresent()).isFalse();
  }

  @Test
  void existsById() {
    Rating savedTrip = ratingRepository.save(rating);
    boolean exists = ratingRepository.existsById(savedTrip.getId());
    assertThat(exists).isTrue();
  }

  @Test
  void findAll() {
    Rating rating1 = ratingRepository.save(rating);

    Rating rating2 = Rating.builder()
            .tripId(2L)  // Уникальный tripId
            .raterType(Rating.RaterType.PASSENGER)
            .score(5)
            .build();
    rating2 = ratingRepository.save(rating2);

    Iterable<Rating> allTrips = ratingRepository.findAll();

    assertThat(allTrips).hasSize(2);
    assertThat(allTrips).extracting(Rating::getId)
            .containsExactlyInAnyOrder(rating1.getId(), rating2.getId());
  }

  @Test
  void count_ShouldReturnCorrectNumber() {
    ratingRepository.save(rating);
    ratingRepository.save(Rating.builder()
            .tripId(3L)  // Уникальный tripId
            .raterType(Rating.RaterType.PASSENGER)
            .score(1)
            .build());

    long count = ratingRepository.count();
    assertThat(count).isEqualTo(2);
  }
}