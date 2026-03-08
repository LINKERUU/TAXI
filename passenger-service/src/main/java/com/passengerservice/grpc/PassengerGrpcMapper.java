package com.passengerservice.grpc;

import com.passengerservice.model.Passenger;
import com.taxi.grpc.passenger.PassengerResponse;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface PassengerGrpcMapper {
  PassengerResponse toGrpc(Passenger passenger);
}
