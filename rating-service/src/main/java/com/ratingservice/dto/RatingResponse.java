package com.ratingservice.dto;


import com.ratingservice.model.enums.RaterType;

public record RatingResponse (
   Long id,
   Long tripId,
   RaterType raterType,
   Integer score,
   String comment
){}