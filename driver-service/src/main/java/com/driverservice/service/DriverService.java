package com.driverservice.service;

import com.driverservice.dto.DriverPatchRequest;
import com.driverservice.dto.DriverRequest;
import com.driverservice.dto.DriverResponse;
import com.driverservice.model.Driver;

public interface DriverService {
  DriverResponse createDriver(DriverRequest request);
  DriverResponse getDriverById(Long id);
  DriverResponse patchDriver(Long id, DriverPatchRequest request);
  void deleteDriver(Long id);
  Driver getExistsDriver(Long id);
}
