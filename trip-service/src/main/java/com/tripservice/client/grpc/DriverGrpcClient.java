package com.tripservice.client.grpc;

import com.taxi.grpc.driver.DriverServiceGrpc;
import com.taxi.grpc.driver.DriverIdRequest;
import com.taxi.grpc.driver.DriverResponse;
import com.taxi.grpc.driver.DriverExistsRequest;
import io.grpc.StatusRuntimeException;
import io.grpc.ManagedChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.grpc.client.GrpcChannelFactory;

@Slf4j
@Component
@RequiredArgsConstructor
public class DriverGrpcClient {

  private final GrpcChannelFactory channelFactory;

  private DriverServiceGrpc.DriverServiceBlockingStub getStub() {
    ManagedChannel channel = channelFactory.createChannel("driver-service");
    return DriverServiceGrpc.newBlockingStub(channel);
  }

  public boolean validateDriver(Long driverId) {
    log.info("gRPC: Validating driver ID: {}", driverId);

    try {
      DriverIdRequest request = DriverIdRequest.newBuilder()
              .setDriverId(driverId)
              .build();

      DriverResponse response = getStub().getDriver(request);

      if (response == null) {
        log.warn("Driver validation failed");
        return false;
      }

      log.info("Driver validated successfully: {}", response.getName());
      return true;

    } catch (StatusRuntimeException e) {
      log.error("gRPC call failed: {}", e.getStatus().getDescription(), e);
      return false;
    }
  }


  public boolean driverExists(Long driverId) {
    try {
      DriverExistsRequest request = DriverExistsRequest.newBuilder()
              .setDriverId(driverId)
              .build();

      return getStub().checkDriverExists(request).getExists();

    } catch (StatusRuntimeException e) {
      log.error("gRPC call failed: {}", e.getStatus().getDescription(), e);
      return false;
    }
  }
}
