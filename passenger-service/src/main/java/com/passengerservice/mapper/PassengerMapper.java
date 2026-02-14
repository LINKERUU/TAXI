package com.passengerservice.mapper;

import com.passengerservice.dto.PassengerRequest;
import com.passengerservice.dto.PassengerResponse;
import com.passengerservice.model.Passenger;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PassengerMapper {

  @Mapping(target = "id")
  @Mapping(target = "name")
  @Mapping(target = "email")
  @Mapping(target = "phone")
  PassengerResponse toPassengerResponse(Passenger passenger);

  @Mapping(target = "id",ignore = true)
  @Mapping(target = "name")
  @Mapping(target = "email")
  @Mapping(target = "phone")
  Passenger toEntity(PassengerRequest request);


  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "name")
  @Mapping(target = "email")
  @Mapping(target = "phone")
  void updatePassengerFromRequest(PassengerRequest request, @MappingTarget Passenger passenger);

}

