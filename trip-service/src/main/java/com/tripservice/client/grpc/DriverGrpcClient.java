package com.tripservice.client.grpc;

import com.taxi.grpc.driver.DriverIdRequest;
import com.taxi.grpc.driver.DriverResponse;
import com.taxi.grpc.driver.DriverServiceGrpc;
import com.tripservice.exception.custom.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DriverGrpcClient {

  private final DriverServiceGrpc.DriverServiceBlockingStub stub;

  @CircuitBreaker(name = "driverService", fallbackMethod = "existsDriverFallback")
  public void existsDriver(Long driverId) {

    DriverIdRequest request = DriverIdRequest.newBuilder()
            .setDriverId(driverId)
            .build();

    DriverResponse response = stub.getDriver(request);

    log.info("Driver validated successfully: {}", response.getId());
  }

  public void existsDriverFallback(Long driverId, Throwable e) {

    log.error("Driver service service fallback. id={}", driverId);

    throw new ServiceUnavailableException("Driver service fallback. id=" + driverId, e);
  }
}
