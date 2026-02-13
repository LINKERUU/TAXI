package com.tripservice.client.grpc;

import com.taxi.grpc.passenger.PassengerServiceGrpc;
import com.taxi.grpc.passenger.PassengerIdRequest;
import com.taxi.grpc.passenger.PassengerResponse;
import com.taxi.grpc.passenger.PassengerExistsRequest;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.grpc.client.GrpcChannelFactory;

@Slf4j
@Component
@RequiredArgsConstructor
public class PassengerGrpcClient {

  private final GrpcChannelFactory channelFactory;

  private PassengerServiceGrpc.PassengerServiceBlockingStub getStub() {
    ManagedChannel channel = channelFactory.createChannel("passenger-service");
    return PassengerServiceGrpc.newBlockingStub(channel);
  }

  public boolean validatePassenger(Long passengerId) {
    log.info("gRPC: Validating passenger ID: {}", passengerId);

    try {
      PassengerIdRequest request = PassengerIdRequest.newBuilder()
              .setPassengerId(passengerId)
              .build();

      PassengerResponse response = getStub().getPassenger(request);

      if (response == null) {
        log.warn("Passenger validation failed");
        return false;
      }

      log.info("Passenger validated successfully: {}", response.getName());
      return true;

    } catch (StatusRuntimeException e) {
      log.error("gRPC call failed: {}", e.getStatus().getDescription(), e);
      return false;
    }
  }

  public boolean passengerExists(Long passengerId) {
    try {
      PassengerExistsRequest request = PassengerExistsRequest.newBuilder()
              .setPassengerId(passengerId)
              .build();

      return getStub().checkPassengerExists(request).getExists();

    } catch (StatusRuntimeException e) {
      log.error("gRPC call failed: {}", e.getStatus().getDescription(), e);
      return false;
    }
  }
}
