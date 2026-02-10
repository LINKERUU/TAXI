package com.passengerservice.grpc;

import com.passengerservice.mapper.PassengerMapper;
import com.passengerservice.repository.PassengerRepository;
import com.taxi.grpc.passenger.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.passengerservice.service.PassengerService;
import org.springframework.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class PassengerGrpcService extends PassengerServiceGrpc.PassengerServiceImplBase {

  private final PassengerService passengerService;
  private final PassengerRepository passengerRepository;

  @Override
  public void getPassenger(PassengerIdRequest request,
                           StreamObserver<PassengerResponse> responseObserver) {
    log.info("gRPC: Get passenger with ID: {}", request.getPassengerId());

    try {
      var passenger = passengerService.getPassengerById(request.getPassengerId());

      PassengerResponse response = PassengerResponse.newBuilder()
              .setId(passenger.getId())
              .setName(passenger.getName())
              .setEmail(passenger.getEmail())
              .setPhone(passenger.getPhone())
              .build();

      responseObserver.onNext(response);
      responseObserver.onCompleted();

    } catch (Exception e) {
      log.error("gRPC error getting passenger: {}", e.getMessage());
      responseObserver.onError(e);
    }
  }

  @Override
  public void checkPassengerExists(PassengerExistsRequest request,
                                   StreamObserver<PassengerExistsResponse> responseObserver) {
    log.info("gRPC: Check passenger exists ID: {}", request.getPassengerId());

    try {
      boolean exists = passengerRepository.existsById(request.getPassengerId());

      PassengerExistsResponse response = PassengerExistsResponse.newBuilder()
              .setExists(exists)
              .build();

      responseObserver.onNext(response);
      responseObserver.onCompleted();

    } catch (Exception e) {
      log.error("gRPC error checking passenger exists: {}", e.getMessage());
      responseObserver.onError(e);
    }
  }
}