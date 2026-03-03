package com.driverservice.controller;

import com.driverservice.dto.CarResponse;
import com.driverservice.dto.DriverPatchRequest;
import com.driverservice.dto.DriverRequest;
import com.driverservice.dto.DriverResponse;
import com.driverservice.exception.custom.DriverNotFoundException;
import com.driverservice.exception.custom.DuplicateEmailException;
import com.driverservice.exception.custom.DuplicateLicensePlateException;
import com.driverservice.service.DriverService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DriverController.class)
class DriverControllerTest {

  private static final String BASE_URL = "/api/drivers";
  private static final String BASE_URL_WITH_ID = BASE_URL + "/{id}";
  private static final Long DRIVER_ID = 1L;
  private static final Long CAR_ID = 1L;

  private static final String NAME = "Иван Петров";
  private static final String EMAIL = "ivan.petrov@example.com";
  private static final String UPDATED_EMAIL = "john.doeh@example.com";
  private static final String PHONE = "+375291234567";
  private static final String UPDATED_NAME = "Иван Петров Обновленный";
  private static final String UPDATED_PHONE = "+375299876543";
  private static final String INVALID_EMAIL = "invalid-email";
  private static final String EMPTY = "";

  private static final String CAR_BRAND = "Toyota";
  private static final String CAR_COLOR = "Черный";
  private static final String CAR_LICENSE_PLATE = "1234 AB-1";
  private static final String UPDATED_CAR_BRAND = "Toyota Camry";
  private static final String UPDATED_CAR_COLOR = "Белый";
  private static final String UPDATED_CAR_LICENSE_PLATE = "5678 CD-2";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private DriverService driverService;

  private String toJson(Object object) {
    return assertDoesNotThrow(() -> objectMapper.writeValueAsString(object));
  }

  private DriverRequest createDriverRequest() {
    return DriverRequest.builder()
            .name(NAME)
            .email(EMAIL)
            .phone(PHONE)
            .carBrand(CAR_BRAND)
            .carColor(CAR_COLOR)
            .carLicensePlate(CAR_LICENSE_PLATE)
            .build();
  }

  private DriverPatchRequest createPatchRequest() {
    return DriverPatchRequest.builder()
            .name(UPDATED_NAME)
            .phone(UPDATED_PHONE)
            .carBrand(UPDATED_CAR_BRAND)
            .carColor(UPDATED_CAR_COLOR)
            .carLicensePlate(UPDATED_CAR_LICENSE_PLATE)
            .build();
  }

  private DriverResponse createDriverResponse() {
    return new DriverResponse(
            DRIVER_ID,
            NAME,
            EMAIL,
            PHONE,
            new CarResponse(CAR_ID, CAR_BRAND, CAR_COLOR, CAR_LICENSE_PLATE)
    );
  }

  private DriverResponse createUpdatedDriverResponse() {
    return new DriverResponse(
            DRIVER_ID,
            UPDATED_NAME,
            EMAIL,
            UPDATED_PHONE,
            new CarResponse(CAR_ID, UPDATED_CAR_BRAND, UPDATED_CAR_COLOR, UPDATED_CAR_LICENSE_PLATE)
    );
  }

  @Test
  @DisplayName("POST should return 201 when driver created successfully")
  public void shouldCreateDriver() {
    DriverRequest request = createDriverRequest();
    DriverResponse response = createDriverResponse();

    when(driverService.createDriver(any(DriverRequest.class))).thenReturn(response);

    assertDoesNotThrow(()-> mockMvc.perform(post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(DRIVER_ID))
            .andExpect(jsonPath("$.name").value(NAME))
            .andExpect(jsonPath("$.email").value(EMAIL))
            .andExpect(jsonPath("$.phone").value(PHONE))
            .andExpect(jsonPath("$.car.id").value(CAR_ID))
            .andExpect(jsonPath("$.car.brand").value(CAR_BRAND))
            .andExpect(jsonPath("$.car.color").value(CAR_COLOR))
            .andExpect(jsonPath("$.car.licensePlate").value(CAR_LICENSE_PLATE)));

    verify(driverService).createDriver(any(DriverRequest.class));
  }

  @Test
  @DisplayName("POST should return 400 when request data is invalid")
  public void shouldReturnBadRequestWhenInvalidData() {
    DriverRequest invalidRequest = DriverRequest.builder()
            .name(EMPTY)
            .email(INVALID_EMAIL)
            .phone(EMPTY)
            .build();

    assertDoesNotThrow(()-> mockMvc.perform(post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(invalidRequest)))
            .andExpect(status().isBadRequest()));

    verify(driverService, never()).createDriver(any());
  }

  @Test
  @DisplayName("POST should return 409 when email already exists")
  public void shouldReturnConflictWhenEmailDuplicate(){
    DriverRequest request = createDriverRequest();

    when(driverService.createDriver(any(DriverRequest.class)))
            .thenThrow(new DuplicateEmailException("Email already exists: " + EMAIL));

    assertDoesNotThrow(()-> mockMvc.perform(post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
            .andExpect(status().isConflict()));

    verify(driverService).createDriver(any(DriverRequest.class));
  }

  @Test
  @DisplayName("POST should return 409 when license plate already exists")
  public void shouldReturnConflictWhenLicensePlateDuplicate(){
    DriverRequest request = createDriverRequest();

    when(driverService.createDriver(any(DriverRequest.class)))
            .thenThrow(new DuplicateLicensePlateException("License plate already exists: " + CAR_LICENSE_PLATE));

    assertDoesNotThrow(()-> mockMvc.perform(post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
            .andExpect(status().isConflict()));

    verify(driverService).createDriver(any(DriverRequest.class));
  }

  @Test
  @DisplayName("GET should return 200 when driver found")
  public void shouldGetDriverById() {
    DriverResponse response = createDriverResponse();

    when(driverService.getDriverById(DRIVER_ID)).thenReturn(response);

    assertDoesNotThrow(()-> mockMvc.perform(get(BASE_URL_WITH_ID, DRIVER_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(DRIVER_ID))
            .andExpect(jsonPath("$.name").value(NAME))
            .andExpect(jsonPath("$.email").value(EMAIL))
            .andExpect(jsonPath("$.phone").value(PHONE))
            .andExpect(jsonPath("$.car.id").value(CAR_ID))
            .andExpect(jsonPath("$.car.brand").value(CAR_BRAND))
            .andExpect(jsonPath("$.car.color").value(CAR_COLOR))
            .andExpect(jsonPath("$.car.licensePlate").value(CAR_LICENSE_PLATE)));

    verify(driverService).getDriverById(DRIVER_ID);
  }

  @Test
  @DisplayName("GET should return 404 when driver not found")
  public void shouldReturnNotFoundWhenDriverMissing() {
    when(driverService.getDriverById(DRIVER_ID))
            .thenThrow(new DriverNotFoundException(DRIVER_ID));

    assertDoesNotThrow(()-> mockMvc.perform(get(BASE_URL_WITH_ID, DRIVER_ID))
            .andExpect(status().isNotFound()));

    verify(driverService).getDriverById(DRIVER_ID);
  }

  @Test
  @DisplayName("PATCH should return 200 when driver updated successfully")
  public void shouldPatchDriver() {
    DriverPatchRequest patchRequest = createPatchRequest();
    DriverResponse updatedResponse = createUpdatedDriverResponse();

    when(driverService.patchDriver(eq(DRIVER_ID), any(DriverPatchRequest.class)))
            .thenReturn(updatedResponse);

    assertDoesNotThrow(()-> mockMvc.perform(patch(BASE_URL_WITH_ID, DRIVER_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(patchRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(DRIVER_ID))
            .andExpect(jsonPath("$.name").value(UPDATED_NAME))
            .andExpect(jsonPath("$.phone").value(UPDATED_PHONE))
            .andExpect(jsonPath("$.car.id").value(CAR_ID))
            .andExpect(jsonPath("$.car.brand").value(UPDATED_CAR_BRAND))
            .andExpect(jsonPath("$.car.color").value(UPDATED_CAR_COLOR))
            .andExpect(jsonPath("$.car.licensePlate").value(UPDATED_CAR_LICENSE_PLATE)));

    verify(driverService).patchDriver(eq(DRIVER_ID), any(DriverPatchRequest.class));
  }

  @Test
  @DisplayName("PATCH should return 404 when driver not found")
  public void shouldReturnNotFoundWhenPatching() {
    DriverPatchRequest patchRequest = DriverPatchRequest.builder()
            .name(UPDATED_NAME)
            .build();

    when(driverService.patchDriver(eq(DRIVER_ID), any(DriverPatchRequest.class)))
            .thenThrow(new DriverNotFoundException(DRIVER_ID));

    assertDoesNotThrow(()-> mockMvc.perform(patch(BASE_URL_WITH_ID, DRIVER_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(patchRequest)))
            .andExpect(status().isNotFound()));
  }

  @Test
  @DisplayName("PATCH should return 409 when email already exists")
  public void shouldReturnConflictWhenPatchingWithDuplicateEmail() {
    DriverRequest patchRequest = DriverRequest.builder()
            .email(UPDATED_EMAIL)
            .build();

    when(driverService.patchDriver(eq(DRIVER_ID), any(DriverPatchRequest.class)))
            .thenThrow(new DuplicateEmailException("Email already exists"));

    assertDoesNotThrow(()-> mockMvc.perform(patch(BASE_URL_WITH_ID, DRIVER_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(patchRequest)))
            .andExpect(status().isConflict()));
  }

  @Test
  @DisplayName("PATCH should return 409 when license plate already exists")
  public void shouldReturnConflictWhenPatchingWithDuplicateLicensePlate() {
    DriverRequest patchRequest = DriverRequest.builder()
            .carLicensePlate(UPDATED_CAR_LICENSE_PLATE)
            .build();

    when(driverService.patchDriver(eq(DRIVER_ID), any(DriverPatchRequest.class)))
            .thenThrow(new DuplicateLicensePlateException("License plate already exists"));

    assertDoesNotThrow(()-> mockMvc.perform(patch(BASE_URL_WITH_ID, DRIVER_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(patchRequest)))
            .andExpect(status().isConflict()));
  }

  @Test
  @DisplayName("DELETE should return 204 when driver deleted successfully")
  public void shouldDeleteDriver() {
    doNothing().when(driverService).deleteDriver(DRIVER_ID);

    assertDoesNotThrow(()-> mockMvc.perform(delete(BASE_URL_WITH_ID, DRIVER_ID))
            .andExpect(status().isNoContent()));

    verify(driverService).deleteDriver(DRIVER_ID);
  }

  @Test
  @DisplayName("DELETE should return 404 when driver not found")
  public void shouldReturnNotFoundWhenDeleting() {
    doThrow(new DriverNotFoundException(DRIVER_ID))
            .when(driverService).deleteDriver(DRIVER_ID);

    assertDoesNotThrow(()-> mockMvc.perform(delete(BASE_URL_WITH_ID, DRIVER_ID))
            .andExpect(status().isNotFound()));
  }
}