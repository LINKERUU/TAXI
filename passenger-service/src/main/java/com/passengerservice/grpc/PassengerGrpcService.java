package com.passengerservice.grpc;

import com.passengerservice.model.Passenger;
import com.passengerservice.service.PassengerService;
import com.taxi.grpc.passenger.PassengerIdRequest;
import com.taxi.grpc.passenger.PassengerResponse;
import com.taxi.grpc.passenger.PassengerServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

@Slf4j
@GrpcService(interceptors = GrpcExceptionInterceptor.class)
@RequiredArgsConstructor
public class PassengerGrpcService extends PassengerServiceGrpc.PassengerServiceImplBase {

  private final PassengerService passengerService;
  private final PassengerGrpcMapper passengerGrpcMapper;

  @Override
  public void getPassenger(PassengerIdRequest request,
                           StreamObserver<PassengerResponse> responseObserver) {
    log.info("gRPC: Get passenger with ID: {}", request.getPassengerId());

    Passenger passenger = passengerService.getExistsPassenger(request.getPassengerId());

    PassengerResponse response = passengerGrpcMapper.toGrpc(passenger);

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}