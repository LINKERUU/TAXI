package com.driverservice.service.impl;

import com.driverservice.dto.DriverRequest;
import com.driverservice.dto.DriverResponse;
import com.driverservice.mapper.DriverMapper;
import com.driverservice.repository.CarRepository;
import com.driverservice.repository.DriverRepository;
import com.driverservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

  private final DriverRepository driverRepository;
  private final CarRepository carRepository;
  private final DriverMapper driverMapper;

  @Override
  @Transactional
  public DriverResponse createDriver(DriverRequest request) {

    if (driverRepository.existsByEmail(request.getEmail())) {
      throw new RuntimeException("Driver with this email already exists");
    }

    if (carRepository.existsByLicensePlate(request.getCarLicensePlate())) {
      throw new RuntimeException("Car with this license plate already exists");
    }

    var car = driverMapper.toCar(request);
    var savedCar = carRepository.save(car);

    var driver = driverMapper.toEntity(request);
    driver.setCar(savedCar);
    var savedDriver = driverRepository.save(driver);

    return driverMapper.toDriverResponse(savedDriver);
  }

  @Override
  @Transactional(readOnly = true)
  public DriverResponse getDriverById(Long id) {
    var driver = driverRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));
    return driverMapper.toDriverResponse(driver);
  }

  @Override
  @Transactional
  public DriverResponse updateDriver(Long id, DriverRequest request) {
    var driver = driverRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));

    driverMapper.updateDriverFromRequest(request,driver);
    driverMapper.updateCarFromRequest(request,driver.getCar());

    var updateDriver = driverRepository.save(driver);

    return driverMapper.toDriverResponse(updateDriver);
  }

  @Override
  @Transactional
  public void deleteDriver(Long id) {
    var driver = driverRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));

    driver.setDeleted(true);

    driverRepository.save(driver);
  }

}
