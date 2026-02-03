package com.tripservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "driver-service",
        configuration = FeignClient.class,
        fallback = DriverClient.DriverClientFallback.class
)
public interface DriverClient {

  @GetMapping("/api/drivers/{driverId}")
  @CircuitBreaker(name = "driverService" ,fallbackMethod = "getDriverByIdFallback")
  DriverResponse getDriverById(@PathVariable("driverId") Long driverId);

  record DriverResponse(Long id, String name, String email, String phone) {}

  @Component
  class DriverClientFallback implements DriverClient {

    @Override
    public DriverResponse getDriverById(Long driverId) {
      return new DriverResponse(
              driverId,
              "Unknown Driver",
              "unknown@email.com",
              "+00000000000"
      );
    }
  }
}