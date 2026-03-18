package com.tripservice.client.grpc;

import com.taxi.grpc.passenger.PassengerIdRequest;
import com.taxi.grpc.passenger.PassengerResponse;
import com.taxi.grpc.passenger.PassengerServiceGrpc;
import com.tripservice.exception.custom.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PassengerGrpcClient {

  private final PassengerServiceGrpc.PassengerServiceBlockingStub stub;

  @CircuitBreaker(name = "passengerService", fallbackMethod = "existsPassengerFallback")
  public void existsPassenger(Long passengerId) {
    log.info("gRPC: Validating passenger ID: {}", passengerId);

    PassengerIdRequest request = PassengerIdRequest.newBuilder().setPassengerId(passengerId).build();
    PassengerResponse response = stub.getPassenger(request);

    log.info("Passenger validated: {}", response.getId());

  }

  public void existsPassengerFallback(Long passengerId, Throwable e) {

    log.warn("Passenger service fallback. id={}", passengerId);

    throw new ServiceUnavailableException("Passenger service fallback. id=" + passengerId, e);
  }
}
