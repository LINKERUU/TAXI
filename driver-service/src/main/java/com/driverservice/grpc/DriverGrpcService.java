package com.driverservice.grpc;

import com.driverservice.model.Driver;
import com.driverservice.service.DriverService;
import com.taxi.grpc.driver.DriverIdRequest;
import com.taxi.grpc.driver.DriverResponse;
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
  private final DriverGrpcMapper driverGrpcMapper;

  @Override
  public void getDriver(DriverIdRequest request,
                        StreamObserver<DriverResponse> responseObserver) {
    log.info("gRPC: Get driver with ID: {}", request.getDriverId());

    Driver driver = driverService.getExistsDriver(request.getDriverId());

    DriverResponse response = driverGrpcMapper.toGrpc(driver);

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}