package com.tripservice.mapper;

import com.tripservice.dto.TripRequest;
import com.tripservice.dto.TripResponse;
import com.tripservice.dto.event.TripCompletedEvent;
import com.tripservice.model.Trip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TripMapper {

  Trip toEntity(TripRequest request);

  @Mapping(target = "orderTime", source = "orderDateTime")
  TripResponse toResponse(Trip trip);

  @Mapping(target="tripId",source = "id")
  TripCompletedEvent toCompletedEvent(Trip trip);
}