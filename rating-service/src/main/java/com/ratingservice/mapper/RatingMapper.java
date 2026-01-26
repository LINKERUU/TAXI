package com.ratingservice.mapper;

import com.ratingservice.dto.RatingRequest;
import com.ratingservice.dto.RatingResponse;
import com.ratingservice.model.Rating;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RatingMapper {

  @Mapping(target = "id", ignore = true)

  Rating toEntity(RatingRequest request);

  RatingResponse toResponse(Rating rating);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  void updateEntityFromRequest(RatingRequest request, @MappingTarget Rating rating);
}