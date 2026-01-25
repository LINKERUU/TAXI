package com.tripservice.service.impl;

import com.tripservice.client.DriverClient;
import com.tripservice.client.PassengerClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalValidationService {

  private final DriverClient driverClient;
  private final PassengerClient passengerClient;

  public void validateDriver(Long driverId) {
    log.info("Validating driver with ID: {}", driverId);

    try {
      DriverClient.DriverResponse driver = driverClient.getDriverById(driverId);
      if (driver == null) {
        throw new IllegalArgumentException("Driver with ID " + driverId + " not found");
      }
      log.info("Driver validated successfully: {} (ID: {})", driver.name(), driver.id());
    } catch (Exception e) {
      log.error("Failed to validate driver with ID {}: {}", driverId, e.getMessage());
      throw new IllegalArgumentException("Driver service error: " + e.getMessage());
    }
  }

  public void validatePassenger(Long passengerId) {
    log.info("Validating passenger with ID: {}", passengerId);

    try {
      PassengerClient.PassengerResponse passenger = passengerClient.getPassengerById(passengerId);
      if (passenger == null) {
        throw new IllegalArgumentException("Passenger with ID " + passengerId + " not found");
      }
      log.info("Passenger validated successfully: {} (ID: {})", passenger.name(), passenger.id());
    } catch (Exception e) {
      log.error("Failed to validate passenger with ID {}: {}", passengerId, e.getMessage());
      throw new IllegalArgumentException("Passenger service error: " + e.getMessage());
    }
  }
}