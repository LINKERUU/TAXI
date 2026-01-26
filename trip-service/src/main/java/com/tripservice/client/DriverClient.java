package com.tripservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "driver-service", url = "${driver.service.url}")
public interface DriverClient {

  @GetMapping("/api/drivers/{driverId}")
  DriverResponse getDriverById(@PathVariable("driverId") Long driverId);

  record DriverResponse(Long id, String name, String email, String phone) {}
}