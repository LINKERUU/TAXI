package com.ratingservice.mapper;

import com.ratingservice.dto.RatingRequest;
import com.ratingservice.dto.RatingResponse;
import com.ratingservice.model.Rating;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RatingMapper {

  Rating toEntity(RatingRequest request);

  RatingResponse toResponse(Rating rating);

}