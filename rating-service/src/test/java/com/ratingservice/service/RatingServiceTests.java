package com.ratingservice.service;

import com.ratingservice.client.TripClient;
import com.ratingservice.dto.RatingRequest;
import com.ratingservice.dto.RatingResponse;
import com.ratingservice.mapper.RatingMapper;
import com.ratingservice.model.Rating;
import com.ratingservice.repository.RatingRepository;
import com.ratingservice.service.consumer.RatingConsumerEvent;
import com.ratingservice.service.impl.RatingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RatingServiceTests {

  @InjectMocks
  private RatingServiceImpl ratingService;

  @Mock
  private RatingRepository ratingRepository;

  @Mock
  private TripClient tripClient;

  @Mock
  private RatingConsumerEvent ratingConsumerEvent;

  @Mock
  private RatingMapper ratingMapper;

  private Rating rating;
  private RatingRequest ratingRequest;
  private RatingResponse ratingResponse;
  private TripClient.TripResponse tripResponse;

  @BeforeEach
  public void setUp() {

    ratingRequest = RatingRequest.builder()
            .tripId(1L)
            .comment("Отлично")
            .raterType(Rating.RaterType.DRIVER)
            .score(5)
            .build();

    ratingResponse = RatingResponse.builder()
            .id(1L)
            .tripId(1L)
            .raterType(Rating.RaterType.DRIVER)
            .comment("Отлично")
            .score(5)
            .build();

    rating = Rating.builder()
            .id(1L)
            .tripId(1L)
            .raterType(Rating.RaterType.DRIVER)
            .comment("Отлично")
            .score(5)
            .build();

    tripResponse = new TripClient.TripResponse(
            1L,
            1L,
            2L,
            "Минск, Ленина, 10",
            "Минск, Независимости, 50",
            TripClient.TripStatus.COMPLETED,
            null,
            java.math.BigDecimal.valueOf(25.50)
    );
  }

  @Test
  void createRating_Success() {
    when(tripClient.getTripById(1L)).thenReturn(tripResponse);
    when(ratingRepository.findByTripIdAndRaterType(1L, Rating.RaterType.DRIVER)).thenReturn(Optional.empty());
    when(ratingMapper.toEntity(ratingRequest)).thenReturn(rating);
    when(ratingRepository.save(rating)).thenReturn(rating);
    when(ratingMapper.toResponse(rating)).thenReturn(ratingResponse);

    RatingResponse result = ratingService.createRating(ratingRequest);

    assertNotNull(result);
    assertEquals(ratingResponse.getId(), result.getId());
    assertEquals(ratingResponse.getComment(), result.getComment());
    assertEquals(ratingResponse.getRaterType(), result.getRaterType());
    assertEquals(ratingResponse.getScore(), result.getScore());
    assertEquals(ratingResponse.getTripId(), result.getTripId());


    verify(tripClient).getTripById(1L);
    verify(ratingRepository).findByTripIdAndRaterType(1L, Rating.RaterType.DRIVER);
    verify(ratingMapper).toEntity(ratingRequest);
    verify(ratingRepository).save(rating);
    verify(ratingMapper).toResponse(rating);
  }

  @Test
  void createRatingFallback_ThrowsRuntimeException() {

    RuntimeException cause = new RuntimeException("Trip service unavailable");

    assertThatThrownBy(() -> ratingService.createRatingFallback(ratingRequest, cause))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Cannot create rating at the moment")
            .hasMessageContaining("Trip service is unavailable");
  }

  @Test
  void createRating_ScoreGreaterThan5_ThrowsException() {

    Long tripId = 1L;

    RatingRequest invalidRequest = RatingRequest.builder()
            .tripId(tripId)
            .raterType(Rating.RaterType.PASSENGER)
            .score(6)
            .comment("Супер")
            .build();

    when(tripClient.getTripById(tripId)).thenReturn(tripResponse);

    assertThatThrownBy(() -> ratingService.createRating(invalidRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Score must be between 1 and 5");
  }

  @Test
  void getRatingById_Success() {

    Long ratingId = 1L;
    Long tripId = 1L;
    when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));
    when(tripClient.getTripById(tripId)).thenReturn(tripResponse);
    when(ratingMapper.toResponse(any(Rating.class))).thenReturn(ratingResponse);

    RatingResponse result = ratingService.getRatingById(ratingId);

    assertNotNull(result);
    assertEquals(ratingResponse.getId(), result.getId());
    assertEquals(ratingResponse.getComment(), result.getComment());
    assertEquals(ratingResponse.getRaterType(), result.getRaterType());
    assertEquals(ratingResponse.getScore(), result.getScore());
    assertEquals(ratingResponse.getTripId(), result.getTripId());


    verify(ratingRepository).findById(ratingId);
    verify(tripClient).getTripById(tripId);
  }

  @Test
  void getRatingByIdFallback_ReturnsFallbackResponse() {

    Long ratingId = 1L;
    Throwable cause = new RuntimeException("Circuit breaker open");
    when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));

    RatingResponse result = ratingService.getRatingByIdFallback(ratingId, cause);

    assertNotNull(result);
    assertEquals(ratingResponse.getId(), result.getId());
    assertEquals(ratingResponse.getComment(), result.getComment());
    assertEquals(ratingResponse.getRaterType(), result.getRaterType());
    assertEquals(ratingResponse.getScore(), result.getScore());
  }

  @Test
  void updateRating_Success() {
    Long ratingId = 1L;
    Long tripId = 1L;

    Rating originalRating = Rating.builder()
            .id(ratingId)
            .tripId(tripId)
            .raterType(Rating.RaterType.PASSENGER)
            .score(5)
            .comment("Отлично")
            .build();

    Rating updatedRating = Rating.builder()
            .id(ratingId)
            .tripId(tripId)
            .raterType(Rating.RaterType.PASSENGER)
            .score(4)
            .comment("Хорошо, но могло быть лучше")
            .build();

    RatingRequest updateRequest = RatingRequest.builder()
            .tripId(tripId)
            .raterType(Rating.RaterType.PASSENGER)
            .score(4)
            .comment("Хорошо, но могло быть лучше")
            .build();

    RatingResponse updatedResponse = RatingResponse.builder()
            .id(ratingId)
            .tripId(tripId)
            .raterType(Rating.RaterType.PASSENGER)
            .score(4)
            .comment("Хорошо, но могло быть лучше")
            .build();

    when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(originalRating));
    when(tripClient.getTripById(tripId)).thenReturn(tripResponse);
    doNothing().when(ratingMapper).updateEntityFromRequest(any(RatingRequest.class), any(Rating.class));
    when(ratingRepository.save(any(Rating.class))).thenReturn(updatedRating);
    when(ratingMapper.toResponse(any(Rating.class))).thenReturn(updatedResponse);

    RatingResponse result = ratingService.updateRating(ratingId, updateRequest);

    assertNotNull(result);
    assertEquals(updatedResponse.getId(), result.getId());
    assertEquals(updatedResponse.getComment(), result.getComment());
    assertEquals(updatedResponse.getRaterType(), result.getRaterType());
    assertEquals(updatedResponse.getScore(), result.getScore());
    assertEquals(updatedResponse.getTripId(), result.getTripId());

    verify(ratingRepository).findById(ratingId);
    verify(tripClient).getTripById(tripId);
    verify(ratingRepository).save(originalRating);
    verify(ratingMapper).updateEntityFromRequest(updateRequest, originalRating);
    verify(ratingMapper).toResponse(updatedRating);
  }

  @Test
  void updateRating_CannotChangeTripId_ThrowsException() {

    Long ratingId = 1L;
    RatingRequest invalidRequest = RatingRequest.builder()
            .tripId(999L)
            .raterType(Rating.RaterType.PASSENGER)
            .score(4)
            .comment("Update")
            .build();

    when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));

    assertThatThrownBy(() -> ratingService.updateRating(ratingId, invalidRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Cannot change trip ID for existing rating");

    verify(ratingRepository, never()).save(any());
  }

  @Test
  void updateRatingFallback_RethrowsRuntimeException() {
    Long ratingId = 1L;
    RuntimeException cause = new RuntimeException("Trip service unavailable");

    assertThatThrownBy(() -> ratingService.updateRatingFallback(ratingId, ratingRequest, cause))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Cannot update rating at the moment")
            .hasMessageContaining("Trip service is unavailable")
            .hasMessageContaining("Please try again later");
  }

  @Test
  void deleteRating_Success() {

    Long ratingId = 1L;
    when(ratingRepository.existsById(ratingId)).thenReturn(true);
    doNothing().when(ratingRepository).deleteById(ratingId);

    ratingService.deleteRating(ratingId);

    verify(ratingRepository).existsById(ratingId);
    verify(ratingRepository).deleteById(ratingId);
  }

  @Test
  void deleteRating_WhenNotFound_ThrowsException() {
    Long ratingId = 1L;

    when(ratingRepository.existsById(ratingId)).thenReturn(false);

    assertThatThrownBy(() -> ratingService.deleteRating(ratingId))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Rating not found with id:");

    verify(ratingRepository).existsById(ratingId);
    verify(ratingRepository, never()).deleteById(anyLong());
  }

  @Test
  void validateRating_ValidScore_Success() {

    Long tripId = 1L;

    RatingRequest validRequest = RatingRequest.builder()
            .tripId(tripId)
            .raterType(Rating.RaterType.PASSENGER)
            .score(3)
            .comment("Нормально")
            .build();

    when(tripClient.getTripById(tripId)).thenReturn(tripResponse);
    when(ratingRepository.findByTripIdAndRaterType(anyLong(), any())).thenReturn(Optional.empty());
    when(ratingMapper.toEntity(any())).thenReturn(rating);
    when(ratingRepository.save(any())).thenReturn(rating);
    when(ratingMapper.toResponse(any())).thenReturn(ratingResponse);

    RatingResponse result = ratingService.createRating(validRequest);

    assertNotNull(result);
    assertEquals(ratingResponse.getId(), result.getId());
    assertEquals(ratingResponse.getComment(), result.getComment());
    assertEquals(ratingResponse.getRaterType(), result.getRaterType());
    assertEquals(ratingResponse.getScore(), result.getScore());

    verify(ratingRepository).findByTripIdAndRaterType(anyLong(), any());
    verify(ratingRepository).save(any());
    verify(ratingMapper).toEntity(any());
    verify(ratingMapper).toResponse(any());
  }

}