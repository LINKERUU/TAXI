package com.driverservice.grpc;

import com.taxi.grpc.driver.DriverResponse;
import com.driverservice.repository.DriverRepository;
import com.driverservice.service.DriverService;
import com.taxi.grpc.driver.DriverExistsRequest;
import com.taxi.grpc.driver.DriverExistsResponse;
import com.taxi.grpc.driver.DriverIdRequest;
import com.taxi.grpc.driver.DriverServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class DriverGrpcService extends DriverServiceGrpc.DriverServiceImplBase {

  private final DriverService driverService;
  private final DriverRepository driverRepository;

  @Override
  public void getDriver(DriverIdRequest request,
                        StreamObserver<DriverResponse> responseObserver) {
    log.info("gRPC: Get driver with ID: {}", request.getDriverId());

    try {
      var driver = driverService.getDriverById(request.getDriverId());

     DriverResponse response = DriverResponse.newBuilder()
              .setId(driver.getId())
              .setName(driver.getName())
              .setEmail(driver.getEmail())
              .setPhone(driver.getPhone())
              .build();

      responseObserver.onNext(response);
      responseObserver.onCompleted();

    } catch (Exception e) {
      log.error("gRPC error getting passenger: {}", e.getMessage());
      responseObserver.onError(e);
    }
  }

  @Override
  public void checkDriverExists(DriverExistsRequest request,
                                StreamObserver<DriverExistsResponse> responseObserver) {
    log.info("gRPC: Check driver exists ID: {}", request.getDriverId());

    try {
      boolean exists = driverRepository.existsById(request.getDriverId());

      DriverExistsResponse response = DriverExistsResponse.newBuilder()
              .setExists(exists)
              .build();

      responseObserver.onNext(response);
      responseObserver.onCompleted();

    } catch (Exception e) {
      log.error("gRPC error checking driver exists: {}", e.getMessage());
      responseObserver.onError(e);
    }
  }
}