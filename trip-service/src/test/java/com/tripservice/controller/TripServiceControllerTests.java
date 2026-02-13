package com.tripservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripservice.dto.StatusUpdateRequest;
import com.tripservice.dto.TripRequest;
import com.tripservice.dto.TripResponse;
import com.tripservice.model.Address;
import com.tripservice.model.Trip;
import com.tripservice.model.enums.TripStatus;
import com.tripservice.service.TripService;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TripServiceControllerTests {

  @Mock
  private TripService tripService;

  @InjectMocks
  private TripController tripController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private TripRequest tripRequest;
  private TripResponse tripResponse;
  private Trip trip;
  private StatusUpdateRequest statusUpdateRequest;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders
            .standaloneSetup(tripController)
            .build();

    objectMapper = new ObjectMapper();

    tripRequest = TripRequest.builder()
            .driverId(1L)
            .passengerId(2L)
            .pickupAddress("Минск, Маркса,20")
            .destinationAddress("Минск, Пролетарская,44")
            .price(BigDecimal.valueOf(25.20))
            .build();

    tripResponse = TripResponse.builder()
            .id(1L)
            .driverId(1L)
            .passengerId(2L)
            .pickupAddress("Минск, Маркса,20")
            .destinationAddress("Минск, Пролетарская,44")
            .status(TripStatus.CREATED)
            .price(BigDecimal.valueOf(25.20))
            .build();

    trip = Trip.builder()
            .id(1L)
            .driverId(1L)
            .passengerId(2L)
            .pickupAddress(createAddress("Минск", "Маркса","20"))
            .destinationAddress(createAddress("Минск", "Пролетарская","44"))
            .status(TripStatus.CREATED)
            .price(BigDecimal.valueOf(25.50))
            .build();

    statusUpdateRequest = StatusUpdateRequest.builder()
            .status(TripStatus.CREATED)
            .build();


  }
  private Address createAddress(String city, String street, String number) {
    return Address.builder()
            .city(city)
            .street(street)
            .buildingNumber(number)
            .build();
  }

  @Test
  void createTrip_Success() throws Exception {
    when(tripService.createTrip(any(TripRequest.class)))
            .thenReturn(tripResponse);

    mockMvc.perform(post("/api/trips")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(tripRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.driverId").value(1L))
            .andExpect(jsonPath("$.passengerId").value(2L))
            .andExpect(jsonPath("$.destinationAddress").value("Минск, Пролетарская,44"))
            .andExpect(jsonPath("$.pickupAddress").value("Минск, Маркса,20"))
            .andExpect(jsonPath("$.status").value("CREATED"))
            .andExpect(jsonPath("$.price").value(25.20));
    verify(tripService, times(1)).createTrip(any(TripRequest.class));
  }


//            .driverId(1L)
//            .passengerId(2L)
//            .pickupAddress("Минск, Маркса,20")
//            .destinationAddress("Минск, Пролетарская,44")
//            .status(TripStatus.CREATED)
//            .price(BigDecimal.valueOf(25.20))
//            .build();



  @Test
  void getTripById_Success() throws Exception {
    Long tripId = 1L;
    when(tripService.getTripById(tripId))
            .thenReturn(tripResponse);

    mockMvc.perform(get("/api/trips/{id}", tripId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.driverId").value(1L))
            .andExpect(jsonPath("$.passengerId").value(2L))
            .andExpect(jsonPath("$.destinationAddress").value("Минск, Пролетарская,44"))
            .andExpect(jsonPath("$.pickupAddress").value("Минск, Маркса,20"))
            .andExpect(jsonPath("$.status").value("CREATED"))
            .andExpect(jsonPath("$.price").value(25.20));

    verify(tripService, times(1)).getTripById(tripId);
  }

  @Test
  void updateTrip_Success() throws Exception {
    Long tripId = 1L;

    TripResponse updatedResponse = TripResponse.builder()
            .destinationAddress("Минск, Комсомольская, 10")
            .build();

    when(tripService.updateTrip(eq(tripId), any(TripRequest.class)))
            .thenReturn(updatedResponse);

    mockMvc.perform(put("/api/trips/{id}", tripId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(tripRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.destinationAddress").value("Минск, Комсомольская, 10"));

    verify(tripService, times(1))
            .updateTrip(eq(tripId), any(TripRequest.class));
  }



  @Test
  void deleteTrip_Success() throws Exception {
   Long tripId = 1L;
   doNothing().when(tripService).deleteTrip(tripId);

    mockMvc.perform(delete("/api/trips/{id}", tripId))
            .andExpect(status().isNoContent());
    verify(tripService, times(1)).deleteTrip(tripId);
  }

  @Test
  void updateTripStatus_Success() throws Exception {

    Long tripId = 1L;
    TripResponse updatedResponse = TripResponse.builder()
            .id(tripId)
            .status(TripStatus.ACCEPTED)
            .build();

    when(tripService.updateTripStatus(eq(tripId), any(StatusUpdateRequest.class)))
            .thenReturn(updatedResponse);

    mockMvc.perform(patch("/api/trips/{id}/status", tripId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(statusUpdateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(tripId))
            .andExpect(jsonPath("$.status").value("ACCEPTED"));

    verify(tripService, times(1)).updateTripStatus(eq(tripId), any(StatusUpdateRequest.class));
  }
}