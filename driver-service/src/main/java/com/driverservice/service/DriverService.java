package com.driverservice.service;

import com.driverservice.dto.DriverRequest;
import com.driverservice.dto.DriverResponse;

public interface DriverService {
  DriverResponse createDriver(DriverRequest request);
  DriverResponse getDriverById(Long id);
  DriverResponse updateDriver(Long id, DriverRequest request);
  void deleteDriver(Long id);
}
