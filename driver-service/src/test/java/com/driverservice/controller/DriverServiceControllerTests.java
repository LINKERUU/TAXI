package com.driverservice.controller;

import com.driverservice.dto.CarResponse;
import com.driverservice.dto.DriverRequest;
import com.driverservice.dto.DriverResponse;
import com.driverservice.model.Car;
import com.driverservice.model.Driver;
import com.driverservice.service.DriverService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class DriverServiceControllerTests {

  @Mock
  private DriverService driverService;

  @InjectMocks
  private DriverController driverController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private DriverRequest driverRequest;
  private DriverResponse driverResponse;
  private Driver driver;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    mockMvc = MockMvcBuilders
            .standaloneSetup(driverController)
            .build();


    driverRequest = DriverRequest.builder()
            .name("Иван Петров")
            .email("ivan.petrov@example.com")
            .phone("+375291234567")
            .carBrand("Toyota")
            .carColor("Черный")
            .carLicensePlate("1234 AB-1")
            .build();

    driverResponse = DriverResponse.builder()
            .id(1L)
            .name("Иван Петров")
            .email("ivan.petrov@example.com")
            .phone("+375291234567")
            .car(CarResponse.builder()
                    .id(1L)
                    .brand("Toyota")
                    .color("Черный")
                    .licensePlate("1234 AB-1")
                    .build())
            .build();

    driver = Driver.builder()
            .id(1L)
            .name("Иван Петров")
            .email("ivan.petrov@example.com")
            .phone("+375291234567")
            .deleted(false)
            .build();
  }

  @Test
  void createPassenger_Success() throws Exception {
    when(driverService.createDriver(any(DriverRequest.class)))
            .thenReturn(driverResponse);

    mockMvc.perform(post("/api/drivers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(driverRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Иван Петров"))
            .andExpect(jsonPath("$.email").value("ivan.petrov@example.com"))
            .andExpect(jsonPath("$.phone").value("+375291234567"))
            .andExpect(jsonPath("$.car.brand").value("Toyota"))
            .andExpect(jsonPath("$.car.color").value("Черный"))
            .andExpect(jsonPath("$.car.licensePlate").value("1234 AB-1"));

    verify(driverService, times(1)).createDriver(any(DriverRequest.class));
  }

  @Test
  void getDriverById_Success() throws Exception {
    Long driverId = 1L;
    when(driverService.getDriverById(driverId)).thenReturn(driverResponse);
    mockMvc.perform(get("/api/drivers/{id}", driverId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Иван Петров"))
            .andExpect(jsonPath("$.email").value("ivan.petrov@example.com"))
            .andExpect(jsonPath("$.phone").value("+375291234567"))
            .andExpect(jsonPath("$.car.brand").value("Toyota"))
            .andExpect(jsonPath("$.car.color").value("Черный"))
            .andExpect(jsonPath("$.car.licensePlate").value("1234 AB-1"));

    verify(driverService, times(1)).getDriverById(driverId);
  }

  @Test
  void updateDriver_Success() throws Exception {
    Long driverId = 1L;

    DriverResponse updatedResponse = DriverResponse.builder()
            .id(driverId)
            .name("Иван Петров Обновленный")
            .email("ivan.petrov@example.com")
            .phone("+375299876543")
            .car(CarResponse.builder()
                    .id(1L)
                    .brand("Toyota Camry")
                    .color("Белый")
                    .licensePlate("5678 CD-2")
                    .build())
            .build();

    when(driverService.updateDriver(eq(driverId), any(DriverRequest.class)))
            .thenReturn(updatedResponse);

    mockMvc.perform(put("/api/drivers/{id}", driverId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(driverRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(driverId))
            .andExpect(jsonPath("$.name").value("Иван Петров Обновленный"))
            .andExpect(jsonPath("$.phone").value("+375299876543"))
            .andExpect(jsonPath("$.car.brand").value("Toyota Camry"))
            .andExpect(jsonPath("$.car.color").value("Белый"))
            .andExpect(jsonPath("$.car.licensePlate").value("5678 CD-2"));


    verify(driverService, times(1))
            .updateDriver(eq(driverId), any(DriverRequest.class));
  }

  @Test
  void deleteDriver_Success() throws Exception {
    Long driverId = 1L;
    doNothing().when(driverService).deleteDriver(driverId);

    mockMvc.perform(delete("/api/drivers/{id}", driverId))
            .andExpect(status().isNoContent());

    verify(driverService, times(1)).deleteDriver(driverId);
  }

}