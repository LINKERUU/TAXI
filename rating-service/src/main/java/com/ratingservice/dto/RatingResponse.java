package com.ratingservice.dto;

import com.ratingservice.model.Rating.RaterType;
import lombok.AllArgsConstructor;
import lombok.Builder;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
public class RatingResponse {
  private Long id;
  private Long tripId;
  private RaterType raterType;
  private Integer score;
  private String comment;
}