package com.ratingservice.dto;

import com.ratingservice.model.Rating.RaterType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RatingResponse {
  private Long id;
  private Long tripId;
  private Long driverId;
  private Long passengerId;
  private RaterType raterType;
  private Integer score;
  private String comment;
}