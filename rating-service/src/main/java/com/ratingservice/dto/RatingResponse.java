package com.ratingservice.dto;

import com.ratingservice.model.Rating.RaterType;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingResponse {
  private Long id;
  private Long tripId;
  private RaterType raterType;
  private Integer score;
  private String comment;
}