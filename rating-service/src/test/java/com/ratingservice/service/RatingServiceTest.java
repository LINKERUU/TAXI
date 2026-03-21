package com.ratingservice.service;

import com.ratingservice.client.TripServiceClient;
import com.ratingservice.client.dto.TripResponse;
import com.ratingservice.client.dto.TripStatus;
import com.ratingservice.dto.RatingPatchRequest;
import com.ratingservice.dto.RatingRequest;
import com.ratingservice.dto.RatingResponse;
import com.ratingservice.exception.custom.RatingNotFoundException;
import com.ratingservice.mapper.RatingMapper;
import com.ratingservice.model.Rating;
import com.ratingservice.model.enums.RaterType;
import com.ratingservice.repository.RatingRepository;
import com.ratingservice.service.impl.RatingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RatingServiceTest {

  private static final Long RATING_ID = 1L;
  private static final Long NON_EXISTENT_ID = 999L;
  private static final Long TRIP_ID = 1L;
  private static final String COMMENT = "хорошая поездка";
  private static final int SCORE = 5;
  private static final int UPDATED_SCORE = 3;
  private static final String UPDATED_COMMENT = "грязный салон";

  @Mock
  private RatingRepository ratingRepository;

  @Mock
  private RatingMapper ratingMapper;

  @Mock
  private TripServiceClient tripServiceClient;

  @InjectMocks
  private RatingServiceImpl ratingService;

  private Rating rating;
  private RatingRequest ratingRequest;
  private RatingResponse ratingResponse;
  private TripResponse tripResponse;

  @BeforeEach
  public void setUp() {
    rating = new Rating(
            TRIP_ID,
            RaterType.PASSENGER,
            SCORE,
            COMMENT
    );

    ratingRequest = RatingRequest.builder()
            .tripId(TRIP_ID)
            .raterType(RaterType.PASSENGER)
            .score(SCORE)
            .comment(COMMENT)
            .build();

    ratingResponse = new RatingResponse(
            RATING_ID,
            TRIP_ID,
            RaterType.PASSENGER,
            SCORE,
            COMMENT
    );

    tripResponse = new TripResponse(
            TRIP_ID,
            TripStatus.COMPLETED
    );

  }

  @Test
  @DisplayName("Should create rating successfully")
  public void createRatingSuccess() {

    when(tripServiceClient.getTripById(TRIP_ID)).thenReturn(tripResponse);
    when(ratingRepository.existsByTripId(TRIP_ID)).thenReturn(false);
    when(ratingMapper.toEntity(ratingRequest)).thenReturn(rating);
    when(ratingRepository.save(rating)).thenReturn(rating);
    when(ratingMapper.toResponse(rating)).thenReturn(ratingResponse);

    RatingResponse result = ratingService.createRating(ratingRequest);

    assertNotNull(result);
    assertEquals(ratingResponse, result);

    verify(tripServiceClient).getTripById(TRIP_ID);
    verify(ratingRepository).existsByTripId(TRIP_ID);
    verify(ratingMapper).toEntity(ratingRequest);
    verify(ratingRepository).save(rating);
    verify(ratingMapper).toResponse(rating);
  }

  @Test
  @DisplayName("Should throw exception if trip is not completed")
  public void createRatingThrowsIfTripNotCompleted() {

    TripResponse notCompletedTrip = new TripResponse(TRIP_ID, TripStatus.CREATED);

    when(tripServiceClient.getTripById(TRIP_ID)).thenReturn(notCompletedTrip);

    assertThrows(IllegalStateException.class,
            () -> ratingService.createRating(ratingRequest));

    verify(ratingRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should throw exception if rating already exists")
  public void createRatingThrowsIfAlreadyExists() {

    when(tripServiceClient.getTripById(TRIP_ID)).thenReturn(tripResponse);
    when(ratingRepository.existsByTripId(TRIP_ID)).thenReturn(true);

    assertThrows(IllegalStateException.class,
            () -> ratingService.createRating(ratingRequest));

    verify(ratingRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should return rating when id exists")
  public void getRatingByIdSuccess() {

    when(ratingRepository.findById(TRIP_ID)).thenReturn(Optional.of(rating));
    when(ratingMapper.toResponse(rating)).thenReturn(ratingResponse);

    RatingResponse result = ratingService.getRatingById(TRIP_ID);

    assertNotNull(result);
    assertEquals(TRIP_ID, result.id());

    verify(ratingRepository).findById(TRIP_ID);
    verify(ratingMapper).toResponse(rating);
  }

  @Test
  @DisplayName("Should throw RatingNotFoundException when rating not found")
  public void getRatingByIdThrowsException() {

    when(ratingRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

    assertThrows(RatingNotFoundException.class,
            () -> ratingService.getRatingById(NON_EXISTENT_ID));

    verify(ratingRepository).findById(NON_EXISTENT_ID);
  }

  @Test
  @DisplayName("Should patch rating successfully")
  public void patchRatingSuccess() {

    RatingPatchRequest patchRequest = RatingPatchRequest.builder()
            .comment(UPDATED_COMMENT)
            .score(UPDATED_SCORE)
            .build();

    when(ratingRepository.findById(RATING_ID)).thenReturn(Optional.of(rating));
    when(ratingMapper.toResponse(rating)).thenReturn(ratingResponse);

    RatingResponse result = ratingService.patchRating(RATING_ID, patchRequest);

    assertNotNull(result);

    verify(ratingRepository).findById(RATING_ID);
    verify(ratingMapper).toResponse(rating);
  }

  @Test
  @DisplayName("Should delete rating when id exists")
  public void deleteRatingSuccess() {

    when(ratingRepository.findById(RATING_ID)).thenReturn(Optional.of(rating));

    ratingService.deleteRating(RATING_ID);

    verify(ratingRepository).deleteById(RATING_ID);
  }

  @Test
  @DisplayName("Should throw exception when deleting non-existent rating")
  void deleteRatingThrows() {

    when(ratingRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

    assertThrows(RatingNotFoundException.class,
            () -> ratingService.deleteRating(NON_EXISTENT_ID));
  }
}