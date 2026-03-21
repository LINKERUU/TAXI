package com.ratingservice.dto;

import com.ratingservice.model.enums.RaterType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RatingRequest {

  @NotNull(message = "Trip ID is required")
  private Long tripId;

  @NotNull(message = "Rater type is required")
  private RaterType raterType;

  @NotNull(message = "Score is required")
  @Min(value = 1, message = "Score must be at least 1")
  @Max(value = 5, message = "Score must be at most 5")
  private Integer score;

  @Size(max = 1000, message = "Comment must not exceed 1000 characters")
  private String comment;
}