package com.ratingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratingservice.dto.RatingRequest;
import com.ratingservice.dto.RatingResponse;
import com.ratingservice.model.Rating;
import com.ratingservice.service.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RatingServiceControllerTests {

  @Mock
  private RatingService ratingService;

  @InjectMocks
  private RatingController ratingController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private RatingRequest ratingRequest;
  private RatingResponse ratingResponse;
  private Rating rating;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    mockMvc = MockMvcBuilders
            .standaloneSetup(ratingController)
            .build();

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
  }

  @Test
  void createRating_Success() throws Exception {

    when(ratingService.createRating(any(RatingRequest.class)))
            .thenReturn(ratingResponse);

    mockMvc.perform(post("/api/ratings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(ratingRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.raterType").value("DRIVER"))
            .andExpect(jsonPath("$.comment").value("Отлично"))
            .andExpect(jsonPath("$.score").value(5));

    verify(ratingService, times(1)).createRating(any(RatingRequest.class));
  }

  @Test
  void getRatingById_Success() throws Exception {
    Long ratingId = 1L;
    when(ratingService.getRatingById(ratingId))
            .thenReturn(ratingResponse);

    mockMvc.perform(get("/api/ratings/{id}", ratingId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.raterType").value("DRIVER"))
            .andExpect(jsonPath("$.comment").value("Отлично"))
            .andExpect(jsonPath("$.score").value(5));

    verify(ratingService, times(1)).getRatingById(ratingId);
  }

  @Test
  void updateRating_Success() throws Exception {
    Long ratingId = 1L;

    RatingResponse updatedResponse = RatingResponse.builder()
            .id(1L)
            .tripId(1L)
            .comment("Не очень")
            .raterType(Rating.RaterType.DRIVER)
            .score(2)
            .build();

    when(ratingService.updateRating(eq(ratingId), any(RatingRequest.class)))
            .thenReturn(updatedResponse);

    mockMvc.perform(put("/api/ratings/{id}", ratingId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(ratingRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.raterType").value("DRIVER"))
            .andExpect(jsonPath("$.comment").value("Не очень"))
            .andExpect(jsonPath("$.score").value(2));

    verify(ratingService, times(1))
            .updateRating(eq(ratingId), any(RatingRequest.class));
  }

  @Test
  void deleteRating_Success() throws Exception {
    Long ratingId = 1L;
    doNothing().when(ratingService).deleteRating(ratingId);

    mockMvc.perform(delete("/api/ratings/{id}", ratingId))
            .andExpect(status().isNoContent());

    verify(ratingService, times(1)).deleteRating(ratingId);
  }

}
