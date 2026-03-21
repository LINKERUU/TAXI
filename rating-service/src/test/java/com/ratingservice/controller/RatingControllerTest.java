package com.ratingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratingservice.dto.RatingPatchRequest;
import com.ratingservice.dto.RatingRequest;
import com.ratingservice.dto.RatingResponse;
import com.ratingservice.exception.custom.RatingNotFoundException;
import com.ratingservice.model.enums.RaterType;
import com.ratingservice.service.RatingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RatingController.class)
public class RatingControllerTest {

  private static final String BASE_URL = "/api/ratings";
  private static final String BASE_URL_WITH_ID = BASE_URL + "/{id}";

  private static final Long RATING_ID = 1L;
  private static final Long NON_EXISTENT_ID = 999L;
  private static final String COMMENT = "хорошая поездка";
  private static final int SCORE = 5;
  private static final int UPDATED_SCORE = 3;
  private static final String UPDATED_COMMENT = "грязный салон";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private RatingService ratingService;


  private String toJson(Object object) throws Exception {
    return objectMapper.writeValueAsString(object);
  }

  private RatingRequest createRatingRequest() {
    return RatingRequest.builder()
            .tripId(RATING_ID)
            .raterType(RaterType.PASSENGER)
            .score(SCORE)
            .comment(COMMENT)
            .build();
  }

  private RatingPatchRequest createPatchRequest() {
    return RatingPatchRequest.builder()
            .score(UPDATED_SCORE)
            .comment(UPDATED_COMMENT)
            .build();
  }

  private RatingResponse createRatingResponse() {
    return new RatingResponse(
            RATING_ID,
            RATING_ID,
            RaterType.PASSENGER,
            SCORE,
            COMMENT
    );
  }

  private RatingResponse createUpdatedRatingResponse() {
    return new RatingResponse(
            RATING_ID,
            RATING_ID,
            RaterType.PASSENGER,
            UPDATED_SCORE,
            UPDATED_COMMENT
    );
  }

  @Test
  @DisplayName("POST should return 201 when rating created")
  public void shouldCreateRating() {

    RatingRequest request = createRatingRequest();
    RatingResponse response = createRatingResponse();

    when(ratingService.createRating(any(RatingRequest.class)))
            .thenReturn(response);

    assertDoesNotThrow(() -> mockMvc.perform(post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(RATING_ID))
            .andExpect(jsonPath("$.tripId").value(RATING_ID))
            .andExpect(jsonPath("$.raterType").value(RaterType.PASSENGER.name()))
            .andExpect(jsonPath("$.score").value(SCORE))
            .andExpect(jsonPath("$.comment").value(COMMENT)));

    verify(ratingService).createRating(any(RatingRequest.class));
  }

  @Test
  @DisplayName("POST should return 400 when request data is invalid")
  public void shouldReturnBadRequestWhenInvalidData() {
    RatingRequest invalidRequest = RatingRequest.builder().build();

    assertDoesNotThrow(() -> mockMvc.perform(post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(invalidRequest)))
            .andExpect(status().isBadRequest()));

    verify(ratingService, never()).createRating(any());
  }

  @Test
  @DisplayName("GET should return rating")
  public void shouldGetRating() {

    RatingResponse response = createRatingResponse();

    when(ratingService.getRatingById(RATING_ID)).thenReturn(response);

    assertDoesNotThrow(() -> mockMvc.perform(get(BASE_URL_WITH_ID, RATING_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(RATING_ID))
            .andExpect(jsonPath("$.tripId").value(RATING_ID))
            .andExpect(jsonPath("$.raterType").value(RaterType.PASSENGER.name()))
            .andExpect(jsonPath("$.score").value(SCORE))
            .andExpect(jsonPath("$.comment").value(COMMENT)));

    verify(ratingService).getRatingById(RATING_ID);
  }

  @Test
  @DisplayName("GET should return 404 when rating not found")
  public void shouldReturnNotFoundWhenDriverMissing() {
    when(ratingService.getRatingById(NON_EXISTENT_ID))
            .thenThrow(new RatingNotFoundException(NON_EXISTENT_ID));

    assertDoesNotThrow(() -> mockMvc.perform(get(BASE_URL_WITH_ID, NON_EXISTENT_ID))
            .andExpect(status().isNotFound()));

    verify(ratingService).getRatingById(NON_EXISTENT_ID);
  }


  @Test
  @DisplayName("PATCH should update rating")
  public void shouldPatchRating() {

    RatingPatchRequest patch = createPatchRequest();
    RatingResponse response = createUpdatedRatingResponse();

    when(ratingService.patchRating(eq(RATING_ID), any()))
            .thenReturn(response);

    assertDoesNotThrow(() -> mockMvc.perform(patch(BASE_URL_WITH_ID, RATING_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(patch)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.comment").value(UPDATED_COMMENT))
            .andExpect(jsonPath("$.score").value(UPDATED_SCORE)));

    verify(ratingService).patchRating(eq(RATING_ID), any());
  }


  @Test
  @DisplayName("PATCH should return 404 when rating not found")
  public void shouldReturnNotFoundWhenPatching() {
    RatingPatchRequest patchRequest = RatingPatchRequest.builder()
            .comment(UPDATED_COMMENT)
            .build();

    when(ratingService.patchRating(eq(NON_EXISTENT_ID), any(RatingPatchRequest.class)))
            .thenThrow(new RatingNotFoundException(NON_EXISTENT_ID));

    assertDoesNotThrow(() -> mockMvc.perform(patch(BASE_URL_WITH_ID, NON_EXISTENT_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(patchRequest)))
            .andExpect(status().isNotFound()));
  }


  @Test
  @DisplayName("DELETE should return 204 when rating deleted successfully")
  public void shouldDeleteRating() {
    doNothing().when(ratingService).deleteRating(RATING_ID);

    assertDoesNotThrow(() -> mockMvc.perform(delete(BASE_URL_WITH_ID, RATING_ID))
            .andExpect(status().isNoContent()));

    verify(ratingService).deleteRating(RATING_ID);
  }

  @Test
  @DisplayName("DELETE should return 404 when rating not found")
  public void shouldReturnNotFoundWhenDeleting() {
    doThrow(new RatingNotFoundException(NON_EXISTENT_ID))
            .when(ratingService).deleteRating(NON_EXISTENT_ID);

    assertDoesNotThrow(() -> mockMvc.perform(delete(BASE_URL_WITH_ID, NON_EXISTENT_ID))
            .andExpect(status().isNotFound()));
  }

}
