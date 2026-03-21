package com.ratingservice.integration;

import com.ratingservice.model.Rating;
import com.ratingservice.model.enums.RaterType;
import com.ratingservice.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RatingRepositoryTest extends TestConfig {

  private static final Long TRIP_ID = 1L;
  private static final String COMMENT = "хорошая поездка";
  private static final int SCORE = 5;
  private static final int UPDATED_SCORE = 3;
  private static final String UPDATED_COMMENT = "грязный салон";

  @Autowired
  RatingRepository ratingRepository;

  private Rating rating;


  @BeforeEach
  public void setUp() {
    rating = new Rating(TRIP_ID, RaterType.PASSENGER, SCORE, COMMENT);
  }


  @Test
  @DisplayName("Should save rating and find by ID")
  public void saveAndFindById() {

    Rating saved = ratingRepository.save(rating);

    Optional<Rating> found = ratingRepository.findById(saved.getId());

    assertThat(found).isPresent();
    assertThat(found.get()).isEqualTo(rating);
    assertThat(found.get().getId()).isEqualTo(rating.getId());
    assertThat(found.get().getComment()).isEqualTo(rating.getComment());
    assertThat(found.get().getScore()).isEqualTo(rating.getScore());
    assertThat(found.get().getTripId()).isEqualTo(rating.getTripId());
  }

  @Test
  @DisplayName("Check existing trip by ID and Rater Type")
  public void checkExistsByTripIdAndRatingType() {

    Rating saved = ratingRepository.save(rating);

    boolean exists = ratingRepository.existsByTripIdAndRaterType(TRIP_ID, RaterType.PASSENGER);
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("Check existing trip by ID")
  public void checkExistsByTripId() {

    Rating saved = ratingRepository.save(rating);

    boolean exists = ratingRepository.existsByTripId(TRIP_ID);
    assertThat(exists).isTrue();
  }


  @Test
  @DisplayName("Should return empty Optional when rating is soft deleted")
  public void shouldReturnEmptyWhenRatingDeleted() {

    Rating saved = ratingRepository.save(rating);
    ratingRepository.delete(saved);

    Optional<Rating> found = ratingRepository.findById(saved.getId());

    assertThat(found).isEmpty();
  }

  @Test
  @DisplayName("Should update rating")
  public void shouldUpdateRating() {
    Rating saved = ratingRepository.save(rating);

    saved.changeComment(UPDATED_COMMENT);
    saved.changeScore(UPDATED_SCORE);

    Rating updated = ratingRepository.save(saved);

    assertThat(updated.getComment()).isEqualTo(UPDATED_COMMENT);
    assertThat(updated.getScore()).isEqualTo(UPDATED_SCORE);
  }


  @Test
  @DisplayName("Should delete rating")
  public void shouldDeleteRating() {

    Rating saved = ratingRepository.save(rating);
    ratingRepository.delete(saved);

    Optional<Rating> deleted = ratingRepository.findById(saved.getId());

    assertThat(deleted).isEmpty();
  }

}
