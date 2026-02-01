package com.driverservice.service.impl;

import com.driverservice.dto.DriverRequest;
import com.driverservice.dto.DriverResponse;
import com.driverservice.model.Car;
import com.driverservice.model.Driver;
import com.driverservice.repository.CarRepository;
import com.driverservice.repository.DriverRepository;
import com.driverservice.service.DriverService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

  private final DriverRepository driverRepository;
  private final CarRepository carRepository;
  private final ModelMapper modelMapper = new ModelMapper();

  @Override
  @Transactional
  public DriverResponse createDriver(DriverRequest request) {

    if (driverRepository.existsByEmail(request.getEmail())) {
      throw new RuntimeException("Driver with this email already exists");
    }

    if (carRepository.existsByLicensePlate(request.getCarLicensePlate())) {
      throw new RuntimeException("Car with this license plate already exists");
    }

    Car car = Car.builder()
            .brand(request.getCarBrand())
            .color(request.getCarColor())
            .licensePlate(request.getCarLicensePlate())
            .build();

    car = carRepository.save(car);

    Driver driver = Driver.builder()
            .name(request.getName())
            .email(request.getEmail())
            .phone(request.getPhone())
            .car(car)
            .build();

    driver = driverRepository.save(driver);

    return modelMapper.map(driver, DriverResponse.class);
  }

  @Override
  public DriverResponse getDriverById(Long id) {
    Driver driver = driverRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));
    return modelMapper.map(driver, DriverResponse.class);
  }

  @Override
  @Transactional
  public DriverResponse updateDriver(Long id, DriverRequest request) {
    Driver driver = driverRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));

    driver.setName(request.getName());
    driver.setEmail(request.getEmail());
    driver.setPhone(request.getPhone());

    Car car = driver.getCar();
    car.setBrand(request.getCarBrand());
    car.setColor(request.getCarColor());
    car.setLicensePlate(request.getCarLicensePlate());

    driverRepository.save(driver);
    carRepository.save(car);

    return modelMapper.map(driver, DriverResponse.class);
  }

  @Override
  @Transactional
  public void deleteDriver(Long id) {
    Driver driver = driverRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));

    driver.setDeleted(true);

    driverRepository.save(driver);
  }

}
