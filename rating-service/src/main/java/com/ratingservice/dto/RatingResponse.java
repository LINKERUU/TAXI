package com.ratingservice.dto;

import com.ratingservice.model.Rating.RaterType;
<<<<<<< Updated upstream
<<<<<<< Updated upstream
=======
import lombok.AllArgsConstructor;
import lombok.Builder;
>>>>>>> Stashed changes
=======
import lombok.Builder;

>>>>>>> Stashed changes
import lombok.Data;

@Data
public class RatingResponse {
  private Long id;
  private Long tripId;
  private RaterType raterType;
  private Integer score;
  private String comment;
}