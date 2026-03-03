package com.driverservice.service;

import com.driverservice.dto.CarResponse;
import com.driverservice.dto.DriverPatchRequest;
import com.driverservice.dto.DriverRequest;
import com.driverservice.dto.DriverResponse;
import com.driverservice.exception.custom.DriverNotFoundException;
import com.driverservice.exception.custom.DuplicateEmailException;
import com.driverservice.exception.custom.DuplicateLicensePlateException;
import com.driverservice.exception.custom.DuplicatePhoneException;
import com.driverservice.mapper.DriverMapper;
import com.driverservice.model.Car;
import com.driverservice.model.Driver;
import com.driverservice.repository.CarRepository;
import com.driverservice.repository.DriverRepository;
import com.driverservice.service.impl.DriverServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DriverServiceTest {

  @Mock
  private DriverRepository driverRepository;

  @Mock
  private CarRepository carRepository;

  @Mock
  private DriverMapper driverMapper;

  @InjectMocks
  private DriverServiceImpl driverService;

  private Driver driver;
  private DriverRequest driverRequest;
  private DriverResponse driverResponse;

  private static final Long DRIVER_ID = 1L;
  private static final Long NON_EXISTENT_ID = 999L;
  private static final Long CAR_ID = 1L;

  private static final String NAME = "John Doe";
  private static final String EMAIL = "john.doe@gmail.com";
  private static final String PHONE = "+375447743555";
  private static final String UPDATED_NAME = "John Updated";
  private static final String UPDATED_PHONE = "+375299876543";
  private static final String UPDATED_EMAIL = "genri.long@gmail.com";
  private static final String NEW_PHONE = "+375331234567";

  private static final String CAR_BRAND = "Toyota";
  private static final String CAR_COLOR = "Black";
  private static final String CAR_LICENSE_PLATE = "1234 AB-1";
  private static final String UPDATED_CAR_BRAND = "Toyota Camry";
  private static final String UPDATED_CAR_COLOR = "White";
  private static final String UPDATED_CAR_LICENSE_PLATE = "5678 CD-2";

  @BeforeEach
  public void setUp() {
    Car car = new Car(CAR_BRAND, CAR_COLOR, CAR_LICENSE_PLATE);
    driver = new Driver(NAME, EMAIL, PHONE, car);

    CarResponse carResponse = new CarResponse(
            CAR_ID,
            CAR_BRAND,
            CAR_COLOR,
            CAR_LICENSE_PLATE
    );

    driverRequest = DriverRequest.builder()
            .name(NAME)
            .email(EMAIL)
            .phone(PHONE)
            .carBrand(CAR_BRAND)
            .carColor(CAR_COLOR)
            .carLicensePlate(CAR_LICENSE_PLATE)
            .build();

    driverResponse = new DriverResponse(
            DRIVER_ID,
            NAME,
            EMAIL,
            PHONE,
            carResponse
    );
  }

  @Test
  @DisplayName("Should create driver successfully when email and license plate are unique")
  public void createDriverSuccess() {
    when(driverRepository.existsByEmailAndDeletedFalse(EMAIL)).thenReturn(false);
    when(carRepository.existsByLicensePlate(CAR_LICENSE_PLATE)).thenReturn(false);
    when(driverMapper.toEntity(driverRequest)).thenReturn(driver);
    when(driverRepository.save(driver)).thenReturn(driver);
    when(driverMapper.toDriverResponse(driver)).thenReturn(driverResponse);

    DriverResponse result = driverService.createDriver(driverRequest);

    assertNotNull(result);
    assertEquals(DRIVER_ID, result.id());
    assertEquals(NAME, result.name());
    assertEquals(EMAIL, result.email());
    assertEquals(PHONE, result.phone());
    assertNotNull(result.car());
    assertEquals(CAR_ID, result.car().id());
    assertEquals(CAR_COLOR, result.car().color());
    assertEquals(CAR_BRAND, result.car().brand());
    assertEquals(CAR_LICENSE_PLATE, result.car().licensePlate());

    verify(driverRepository).existsByEmailAndDeletedFalse(EMAIL);
    verify(carRepository).existsByLicensePlate(CAR_LICENSE_PLATE);
    verify(driverMapper).toEntity(driverRequest);
    verify(driverRepository).save(driver);
    verify(driverMapper).toDriverResponse(driver);
  }

  @Test
  @DisplayName("Should throw DuplicateEmailException when creating driver with existing email")
  public void createDriverThrowsExceptionWhenEmailExists() {
    when(driverRepository.existsByEmailAndDeletedFalse(EMAIL)).thenReturn(true);

    assertThrows(DuplicateEmailException.class,
            () -> driverService.createDriver(driverRequest));

    verify(driverRepository).existsByEmailAndDeletedFalse(EMAIL);
    verify(carRepository, never()).existsByLicensePlate(anyString());
    verify(driverMapper, never()).toEntity(any());
    verify(driverRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should throw DuplicateLicensePlateException when creating driver with existing license plate")
  public void createDriverThrowsExceptionWhenLicensePlateExists() {
    when(driverRepository.existsByEmailAndDeletedFalse(EMAIL)).thenReturn(false);
    when(carRepository.existsByLicensePlate(CAR_LICENSE_PLATE)).thenReturn(true);

    assertThrows(DuplicateLicensePlateException.class,
            () -> driverService.createDriver(driverRequest));

    verify(driverRepository).existsByEmailAndDeletedFalse(EMAIL);
    verify(carRepository).existsByLicensePlate(CAR_LICENSE_PLATE);
    verify(driverMapper, never()).toEntity(any());
    verify(driverRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should return driver when ID exists")
  public void getDriverByIdSuccess() {
    when(driverRepository.findByIdAndDeletedFalse(DRIVER_ID))
            .thenReturn(Optional.of(driver));
    when(driverMapper.toDriverResponse(driver)).thenReturn(driverResponse);

    DriverResponse result = driverService.getDriverById(DRIVER_ID);

    assertNotNull(result);
    assertEquals(DRIVER_ID, result.id());
    assertEquals(NAME, result.name());
    assertEquals(EMAIL, result.email());
    assertEquals(PHONE, result.phone());

    verify(driverRepository).findByIdAndDeletedFalse(DRIVER_ID);
    verify(driverMapper).toDriverResponse(driver);
  }

  @Test
  @DisplayName("Should throw DriverNotFoundException when ID does not exist")
  public void getDriverByIdThrowsExceptionWhenNotFound() {
    when(driverRepository.findByIdAndDeletedFalse(NON_EXISTENT_ID))
            .thenReturn(Optional.empty());

    DriverNotFoundException exception = assertThrows(DriverNotFoundException.class,
            () -> driverService.getDriverById(NON_EXISTENT_ID));

    assertEquals("Driver not found with id " + NON_EXISTENT_ID, exception.getMessage());
    verify(driverRepository).findByIdAndDeletedFalse(NON_EXISTENT_ID);
    verify(driverMapper, never()).toDriverResponse(any());
  }

  @Test
  @DisplayName("Should update driver fields when valid patch request provided")
  public void patchDriverSuccess() {
    DriverPatchRequest patchRequest = DriverPatchRequest.builder()
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .carBrand(UPDATED_CAR_BRAND)
            .carColor(UPDATED_CAR_COLOR)
            .carLicensePlate(UPDATED_CAR_LICENSE_PLATE)
            .build();

    when(driverRepository.findByIdAndDeletedFalse(DRIVER_ID))
            .thenReturn(Optional.of(driver));
    when(driverRepository.existsByEmailAndDeletedFalse(UPDATED_EMAIL)).thenReturn(false);
    when(driverRepository.existsByPhoneAndDeletedFalse(UPDATED_PHONE)).thenReturn(false);
    when(carRepository.existsByLicensePlate(UPDATED_CAR_LICENSE_PLATE)).thenReturn(false);
    when(driverMapper.toDriverResponse(driver)).thenReturn(driverResponse);

    DriverResponse result = driverService.patchDriver(DRIVER_ID, patchRequest);

    assertNotNull(result);
    assertEquals(DRIVER_ID, result.id());
    assertEquals(NAME, result.name());
    assertEquals(EMAIL, result.email());
    assertEquals(PHONE, result.phone());

    verify(driverRepository).findByIdAndDeletedFalse(DRIVER_ID);
    verify(driverRepository).existsByEmailAndDeletedFalse(UPDATED_EMAIL);
    verify(driverRepository).existsByPhoneAndDeletedFalse(UPDATED_PHONE);
    verify(carRepository).existsByLicensePlate(UPDATED_CAR_LICENSE_PLATE);
    verify(driverMapper).toDriverResponse(driver);
  }

  @Test
  @DisplayName("Should update only provided fields in patch request")
  public void patchDriverPartialUpdate() {
    DriverPatchRequest patchRequest = DriverPatchRequest.builder()
            .name(UPDATED_NAME)
            .phone(UPDATED_PHONE)
            .carColor(UPDATED_CAR_COLOR)
            .build();

    when(driverRepository.findByIdAndDeletedFalse(DRIVER_ID))
            .thenReturn(Optional.of(driver));
    when(driverRepository.existsByPhoneAndDeletedFalse(UPDATED_PHONE)).thenReturn(false);
    when(driverMapper.toDriverResponse(driver)).thenReturn(driverResponse);

    DriverResponse result = driverService.patchDriver(DRIVER_ID, patchRequest);

    assertNotNull(result);
    assertEquals(DRIVER_ID, result.id());
    assertEquals(NAME, result.name());
    assertEquals(EMAIL, result.email());
    assertEquals(PHONE, result.phone());

    verify(driverRepository).findByIdAndDeletedFalse(DRIVER_ID);
    verify(driverRepository).existsByPhoneAndDeletedFalse(UPDATED_PHONE);
    verify(driverRepository, never()).existsByEmailAndDeletedFalse(anyString());
    verify(carRepository, never()).existsByLicensePlate(anyString());
    verify(driverMapper).toDriverResponse(driver);
  }

  @Test
  @DisplayName("Should throw DuplicateEmailException when patching with existing email")
  public void patchDriverThrowsExceptionWhenEmailExists() {
    DriverPatchRequest patchRequest = DriverPatchRequest.builder()
            .email(UPDATED_EMAIL)
            .build();

    when(driverRepository.findByIdAndDeletedFalse(DRIVER_ID))
            .thenReturn(Optional.of(driver));
    when(driverRepository.existsByEmailAndDeletedFalse(UPDATED_EMAIL)).thenReturn(true);

    assertThrows(DuplicateEmailException.class,
            () -> driverService.patchDriver(DRIVER_ID, patchRequest));

    verify(driverRepository).findByIdAndDeletedFalse(DRIVER_ID);
    verify(driverRepository).existsByEmailAndDeletedFalse(UPDATED_EMAIL);
  }

  @Test
  @DisplayName("Should throw DuplicatePhoneException when patching with existing phone")
  public void patchDriverThrowsExceptionWhenPhoneExists() {
    DriverPatchRequest patchRequest = DriverPatchRequest.builder()
            .phone(NEW_PHONE)
            .build();

    when(driverRepository.findByIdAndDeletedFalse(DRIVER_ID))
            .thenReturn(Optional.of(driver));
    when(driverRepository.existsByPhoneAndDeletedFalse(NEW_PHONE)).thenReturn(true);

    assertThrows(DuplicatePhoneException.class,
            () -> driverService.patchDriver(DRIVER_ID, patchRequest));

    verify(driverRepository).findByIdAndDeletedFalse(DRIVER_ID);
    verify(driverRepository).existsByPhoneAndDeletedFalse(NEW_PHONE);
  }

  @Test
  @DisplayName("Should throw DuplicateLicensePlateException when patching with existing license plate")
  public void patchDriverThrowsExceptionWhenLicensePlateExists() {
    DriverPatchRequest patchRequest = DriverPatchRequest.builder()
            .carLicensePlate(UPDATED_CAR_LICENSE_PLATE)
            .build();

    when(driverRepository.findByIdAndDeletedFalse(DRIVER_ID))
            .thenReturn(Optional.of(driver));
    when(carRepository.existsByLicensePlate(UPDATED_CAR_LICENSE_PLATE)).thenReturn(true);

    assertThrows(DuplicateLicensePlateException.class,
            () -> driverService.patchDriver(DRIVER_ID, patchRequest));

    verify(driverRepository).findByIdAndDeletedFalse(DRIVER_ID);
    verify(carRepository).existsByLicensePlate(UPDATED_CAR_LICENSE_PLATE);
  }

  @Test
  @DisplayName("Should throw DriverNotFoundException when patching non-existent driver")
  public void patchDriverThrowsExceptionWhenDriverNotFound() {
    DriverPatchRequest patchRequest = DriverPatchRequest.builder()
            .name(UPDATED_NAME)
            .build();

    when(driverRepository.findByIdAndDeletedFalse(NON_EXISTENT_ID))
            .thenReturn(Optional.empty());

    assertThrows(DriverNotFoundException.class,
            () -> driverService.patchDriver(NON_EXISTENT_ID, patchRequest));

    verify(driverRepository).findByIdAndDeletedFalse(NON_EXISTENT_ID);
    verify(driverRepository, never()).existsByEmailAndDeletedFalse(anyString());
    verify(driverRepository, never()).existsByPhoneAndDeletedFalse(anyString());
    verify(carRepository, never()).existsByLicensePlate(anyString());
  }

  @Test
  @DisplayName("Should soft delete driver when ID exists")
  public void deleteDriverSuccess() {
    when(driverRepository.findByIdAndDeletedFalse(DRIVER_ID))
            .thenReturn(Optional.of(driver));

    driverService.deleteDriver(DRIVER_ID);

    assertTrue(driver.isDeleted());
    verify(driverRepository).findByIdAndDeletedFalse(DRIVER_ID);
  }

  @Test
  @DisplayName("Should throw DriverNotFoundException when deleting non-existent driver")
  public void deleteDriverThrowsExceptionWhenDriverNotFound() {
    when(driverRepository.findByIdAndDeletedFalse(NON_EXISTENT_ID))
            .thenReturn(Optional.empty());

    DriverNotFoundException exception = assertThrows(DriverNotFoundException.class,
            () -> driverService.deleteDriver(NON_EXISTENT_ID));

    assertEquals("Driver not found with id " + NON_EXISTENT_ID, exception.getMessage());
    verify(driverRepository).findByIdAndDeletedFalse(NON_EXISTENT_ID);
  }
}