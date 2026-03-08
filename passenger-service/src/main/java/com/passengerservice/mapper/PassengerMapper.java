package com.passengerservice.mapper;

import com.passengerservice.dto.PassengerRequest;
import com.passengerservice.dto.PassengerResponse;
import com.passengerservice.model.Passenger;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface PassengerMapper {

  PassengerResponse toPassengerResponse(Passenger passenger);

  Passenger toEntity(PassengerRequest request);
}

