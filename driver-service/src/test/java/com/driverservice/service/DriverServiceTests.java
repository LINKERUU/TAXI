package com.driverservice.service;

import com.driverservice.dto.CarResponse;
import com.driverservice.dto.DriverRequest;
import com.driverservice.dto.DriverResponse;
import com.driverservice.mapper.DriverMapper;
import com.driverservice.model.Car;
import com.driverservice.model.Driver;
import com.driverservice.repository.CarRepository;
import com.driverservice.repository.DriverRepository;
import com.driverservice.service.impl.DriverServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DriverServiceTests {

  @Mock
  private DriverRepository driverRepository;

  @Mock
  private CarRepository carRepository;

  @Mock
  private DriverMapper driverMapper;

  @InjectMocks
  private DriverServiceImpl driverService;

  private DriverRequest driverRequest;
  private DriverResponse driverResponse;
  private Driver driver;
  private Car car;
  private CarResponse carResponse;

  @BeforeEach
  void setUp() {
    car = Car.builder()
            .id(1L)
            .brand("Toyota")
            .color("Black")
            .licensePlate("1234 AB-1")
            .build();

    carResponse = CarResponse.builder()
            .id(1L)
            .brand("Toyota")
            .color("Black")
            .licensePlate("1234 AB-2")
            .build();

    driver = Driver.builder()
            .id(1L)
            .name("John Doe")
            .email("john.doe@example.com")
            .phone("+375291234567")
            .car(car)
            .deleted(false)
            .build();

    driverRequest = DriverRequest.builder()
            .name("John Doe")
            .email("john.doe@example.com")
            .phone("+375291234567")
            .carBrand("Toyota")
            .carColor("Black")
            .carLicensePlate("1234 AB-2")
            .build();


    driverResponse = DriverResponse.builder()
            .id(1L)
            .name("John Doe")
            .email("john.doe@example.com")
            .phone("+375291234567")
            .car(carResponse)
            .build();
  }

  @Test
  void createDriver_Success() {

    when(driverRepository.existsByEmail(driverRequest.getEmail())).thenReturn(false);
    when(carRepository.existsByLicensePlate(driverRequest.getCarLicensePlate())).thenReturn(false);
    when(driverMapper.toCar(any(DriverRequest.class))).thenReturn(car);
    when(carRepository.save(any(Car.class))).thenReturn(car);
    when(driverMapper.toEntity(any(DriverRequest.class))).thenReturn(driver);
    when(driverRepository.save(any(Driver.class))).thenReturn(driver);
    when(driverMapper.toDriverResponse(any(Driver.class))).thenReturn(driverResponse);

    DriverResponse result = driverService.createDriver(driverRequest);

    assertNotNull(result);
    assertEquals(driverResponse.getId(), result.getId());
    assertEquals(driverResponse.getName(), result.getName());
    assertEquals(driverResponse.getEmail(), result.getEmail());
    assertEquals(driverResponse.getPhone(), result.getPhone());
    assertNotNull(result.getCar());
    assertEquals(driverResponse.getCar().getLicensePlate(), result.getCar().getLicensePlate());

    verify(driverRepository, times(1)).existsByEmail(driverRequest.getEmail());
    verify(carRepository, times(1)).existsByLicensePlate(driverRequest.getCarLicensePlate());
    verify(driverMapper, times(1)).toCar(driverRequest);
    verify(carRepository, times(1)).save(car);
    verify(driverMapper, times(1)).toEntity(driverRequest);
    verify(driverRepository, times(1)).save(driver);
    verify(driverMapper, times(1)).toDriverResponse(driver);
  }

  @Test
  void createDriver_EmailAlreadyExists_ThrowsException() {
    when(driverRepository.existsByEmail(driverRequest.getEmail())).thenReturn(true);

    RuntimeException exception = assertThrows(RuntimeException.class,
            () -> driverService.createDriver(driverRequest));

    assertEquals("Driver with this email already exists", exception.getMessage());

    verify(driverRepository, times(1)).existsByEmail(driverRequest.getEmail());
    verify(carRepository, never()).existsByLicensePlate(anyString());
    verify(driverMapper, never()).toCar(any());
    verify(carRepository, never()).save(any());
    verify(driverMapper, never()).toEntity(any());
    verify(driverRepository, never()).save(any());
  }

  @Test
  void createDriver_LicensePlateAlreadyExists_ThrowsException() {

    when(driverRepository.existsByEmail(driverRequest.getEmail())).thenReturn(false);
    when(carRepository.existsByLicensePlate(driverRequest.getCarLicensePlate())).thenReturn(true);

    RuntimeException exception = assertThrows(RuntimeException.class,
            () -> driverService.createDriver(driverRequest));

    assertEquals("Car with this license plate already exists", exception.getMessage());

    verify(driverRepository, times(1)).existsByEmail(driverRequest.getEmail());
    verify(carRepository, times(1)).existsByLicensePlate(driverRequest.getCarLicensePlate());
    verify(driverMapper, never()).toCar(any());
    verify(carRepository, never()).save(any());
  }

  @Test
  void getDriverById_Success() {

    Long driverId = 1L;
    when(driverRepository.findByIdAndDeletedFalse(driverId)).thenReturn(Optional.of(driver));
    when(driverMapper.toDriverResponse(any(Driver.class))).thenReturn(driverResponse);

    DriverResponse result = driverService.getDriverById(driverId);

    assertNotNull(result);
    assertEquals(driverId, result.getId());
    assertEquals(driver.getName(), result.getName());
    assertEquals(driver.getEmail(), result.getEmail());

    verify(driverRepository, times(1)).findByIdAndDeletedFalse(driverId);
    verify(driverMapper, times(1)).toDriverResponse(driver);
  }

  @Test
  void updateDriver_Success() {
    Long driverId = 1L;

    DriverRequest updateRequest = DriverRequest.builder()
            .name("John Updated")
            .phone("+375299876543")
            .carBrand("BMW")
            .carColor("White")
            .carLicensePlate("5678 CD-2")
            .email("john.doe@example.com")
            .build();

    Driver existingDriver = Driver.builder()
            .id(driverId)
            .name("John Doe")
            .email("john.doe@example.com")
            .phone("+375291234567")
            .car(car)
            .deleted(false)
            .build();

    DriverResponse expectedResponse = DriverResponse.builder()
            .id(driverId)
            .name("John Updated")
            .email("john.doe@example.com")
            .phone("+375299876543")
            .car(CarResponse.builder()
                    .id(1L)
                    .brand("BMW")
                    .color("White")
                    .licensePlate("5678 CD-2")
                    .build())
            .build();

    when(driverRepository.findByIdAndDeletedFalse(driverId))
            .thenReturn(Optional.of(existingDriver));
    when(driverRepository.save(any(Driver.class)))
            .thenReturn(existingDriver);
    when(driverMapper.toDriverResponse(any(Driver.class)))
            .thenReturn(expectedResponse);

    DriverResponse result = driverService.updateDriver(driverId, updateRequest);

    assertEquals(expectedResponse, result);

    verify(driverRepository).findByIdAndDeletedFalse(driverId);
    verify(driverMapper).updateDriverFromRequest(updateRequest, existingDriver);
    verify(driverMapper).updateCarFromRequest(updateRequest, car);
    verify(driverRepository).save(existingDriver);
    verify(driverMapper).toDriverResponse(existingDriver);
  }

  @Test
  void deleteDriver_Success() {
    Long driverId = 1L;

    Driver driverToDelete = Driver.builder()
            .id(driverId)
            .name("John Doe")
            .email("john.doe@example.com")
            .deleted(false)
            .build();

    when(driverRepository.findById(driverId)).thenReturn(Optional.of(driverToDelete));
    when(driverRepository.save(any(Driver.class))).thenReturn(driverToDelete);

    driverService.deleteDriver(driverId);

    assertTrue(driverToDelete.isDeleted());
    verify(driverRepository, times(1)).findById(driverId);
    verify(driverRepository, times(1)).save(driverToDelete);
  }

}
