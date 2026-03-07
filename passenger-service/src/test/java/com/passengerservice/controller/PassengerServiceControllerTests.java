package com.passengerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.passengerservice.dto.PassengerRequest;
import com.passengerservice.dto.PassengerResponse;
import com.passengerservice.model.Passenger;
import com.passengerservice.service.PassengerService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PassengerServiceControllerTests {

  @Mock
  private PassengerService passengerService;

  @InjectMocks
  private PassengerController passengerController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private PassengerRequest passengerRequest;
  private PassengerResponse passengerResponse;
  private Passenger passenger;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    mockMvc = MockMvcBuilders
            .standaloneSetup(passengerController)
            .build();

    passengerRequest = PassengerRequest.builder()
            .name("John Doe")
            .email("john.doe@example.com")
            .phone("+375291234567")
            .build();

    passengerResponse = PassengerResponse.builder()
            .id(1L)
            .name("John Doe")
            .email("john.doe@example.com")
            .phone("+375291234567")
            .build();

    passenger = Passenger.builder()
            .id(1L)
            .name("John Doe")
            .email("john.doe@example.com")
            .phone("+375291234567")
            .deleted(false)
            .build();
  }

  @Test
  void createPassenger_Success() throws Exception {
    when(passengerService.createPassenger(any(PassengerRequest.class)))
            .thenReturn(passengerResponse);

    mockMvc.perform(post("/api/passengers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(passengerRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("John Doe"))
            .andExpect(jsonPath("$.email").value("john.doe@example.com"))
            .andExpect(jsonPath("$.phone").value("+375291234567"));

    verify(passengerService, times(1)).createPassenger(any(PassengerRequest.class));
  }

  @Test
  void getPassengerById_Success() throws Exception {
    Long passengerId = 1L;
    when(passengerService.getPassengerById(passengerId))
            .thenReturn(passengerResponse);

    mockMvc.perform(get("/api/passengers/{id}", passengerId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("John Doe"))
            .andExpect(jsonPath("$.email").value("john.doe@example.com"));

    verify(passengerService, times(1)).getPassengerById(passengerId);
  }

  @Test
  void updatePassenger_Success() throws Exception {
    Long passengerId = 1L;

    PassengerResponse updatedResponse = PassengerResponse.builder()
            .id(passengerId)
            .name("John Updated")
            .email("john.doe@example.com")
            .phone("+375299876543")
            .build();

    when(passengerService.updatePassenger(eq(passengerId), any(PassengerRequest.class)))
            .thenReturn(updatedResponse);

    mockMvc.perform(put("/api/passengers/{id}", passengerId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(passengerRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(passengerId))
            .andExpect(jsonPath("$.name").value("John Updated"))
            .andExpect(jsonPath("$.phone").value("+375299876543"));

    verify(passengerService, times(1))
            .updatePassenger(eq(passengerId), any(PassengerRequest.class));
  }

  @Test
  void deletePassenger_Success() throws Exception {
    Long passengerId = 1L;
    doNothing().when(passengerService).deletePassenger(passengerId);

    mockMvc.perform(delete("/api/passengers/{id}", passengerId))
            .andExpect(status().isNoContent());

    verify(passengerService, times(1)).deletePassenger(passengerId);
  }
}