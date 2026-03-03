package com.driverservice.service.impl;

import com.driverservice.dto.DriverPatchRequest;
import com.driverservice.dto.DriverRequest;
import com.driverservice.dto.DriverResponse;
import com.driverservice.exception.custom.DriverNotFoundException;
import com.driverservice.exception.custom.DuplicateEmailException;
import com.driverservice.exception.custom.DuplicateLicensePlateException;
import com.driverservice.exception.custom.DuplicatePhoneException;
import com.driverservice.mapper.DriverMapper;
import com.driverservice.model.Driver;
import com.driverservice.repository.CarRepository;
import com.driverservice.repository.DriverRepository;
import com.driverservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

    validateEmailNotExists(request.getEmail());
    validateLicensePlateNotExists(request.getCarLicensePlate());

    Driver driver = driverMapper.toEntity(request);

    driverRepository.save(driver);

    return driverMapper.toDriverResponse(driver);
  }


  @Override
  public DriverResponse getDriverById(Long id) {
    log.info("Getting driver with ID: {}", id);
    return driverMapper.toDriverResponse(getExistsDriver(id));
  }

  @Override
  @Transactional
  public DriverResponse patchDriver(Long id, DriverPatchRequest request) {
    log.info("Updating passenger with ID: {}", id);

    Driver driver = getExistsDriver(id);

    applyPatch(driver, request);

    log.info("Passenger with ID {} updated", id);

    return driverMapper.toDriverResponse(driver);
  }

  @Override
  @Transactional
  public void deleteDriver(Long id) {

    log.info("Soft deleting driver with ID: {}", id);

    Driver driver = getExistsDriver(id);

    driver.markAsDeleted();

    log.info("Driver with ID {} soft deleted", id);
  }

  @Override
  public Driver getExistsDriver(Long id){
    return driverRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new DriverNotFoundException(id));
  }

  private void validateEmailNotExists(String email) {
    if(driverRepository.existsByEmailAndDeletedFalse(email)){
      throw new DuplicateEmailException("Email already exists: " + email);
    }
  }

  private void validateLicensePlateNotExists(String plate) {
    if(carRepository.existsByLicensePlate(plate)){
      throw new DuplicateLicensePlateException("Car with this license plate already exists");
    }
  }

  private void validatePhoneNotExists(String phone) {
    if(driverRepository.existsByPhoneAndDeletedFalse(phone)){
      throw new DuplicatePhoneException("Phone already exists: " + phone);
    }
  }

  private void applyPatch(Driver driver, DriverPatchRequest request) {

    Optional.ofNullable(request.getName())
            .filter(name -> !name.isBlank())
            .ifPresent(driver::changeName);

    Optional.ofNullable(request.getEmail())
            .filter(email -> !email.isBlank())
            .ifPresent(email -> {
              validateEmailNotExists(email);
              driver.changeEmail(email);
            });

    Optional.ofNullable(request.getPhone())
            .filter(phone -> !phone.isBlank())
            .ifPresent(phone ->{
              validatePhoneNotExists(phone);
              driver.changePhone(phone);
            });

    Optional.ofNullable(request.getCarBrand())
            .filter(carBrand -> !carBrand.isBlank())
            .ifPresent(driver.getCar()::changeBrand);

    Optional.ofNullable(request.getCarColor())
            .filter(carColor -> !carColor.isBlank())
            .ifPresent(driver.getCar()::changeColor);

    Optional.ofNullable(request.getCarLicensePlate())
            .filter(carLicensePlate -> !carLicensePlate.isBlank())
            .ifPresent(carLicensePlate ->{
              validateLicensePlateNotExists(carLicensePlate);
              driver.getCar().changeLicensePlate(carLicensePlate);
            });
  }
}