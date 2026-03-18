package com.tripservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tripservice.dto.*;
import com.tripservice.exception.custom.TripNotFoundException;
import com.tripservice.model.Address;
import com.tripservice.model.enums.TripStatus;
import com.tripservice.service.TripService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TripController.class)
public class TripServiceControllerTest {

  private static final String BASE_URL = "/api/trips";
  private static final String BASE_URL_WITH_ID = BASE_URL + "/{id}";
  private static final String STATUS_URL = BASE_URL + "/{id}/status";

  private static final Long TRIP_ID = 1L;
  private static final Long DRIVER_ID = 1L;
  private static final Long PASSENGER_ID = 1L;

  private static final String PICKUP_CITY = "Минск";
  private static final String PICKUP_STREET = "Л.Беды";
  private static final String PICKUP_NUMBER = "4";
  private static final String DESTINATION_CITY = "Минск";
  private static final String DESTINATION_STREET = "Ленина";
  private static final String DESTINATION_NUMBER = "7";

  private static final BigDecimal PRICE = BigDecimal.valueOf(10.84);
  private static final BigDecimal UPDATED_PRICE = BigDecimal.valueOf(5.84);
  private static final Long UPDATED_DRIVER_ID = 2L;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private TripService tripService;

  private String toJson(Object object) throws Exception {
    return objectMapper.writeValueAsString(object);
  }

  private TripRequest createTripRequest() {
    return TripRequest.builder()
            .passengerId(PASSENGER_ID)
            .driverId(DRIVER_ID)
            .pickupAddress(new AddressRequest(PICKUP_CITY, PICKUP_STREET, PICKUP_NUMBER))
            .destinationAddress(new AddressRequest(DESTINATION_CITY, DESTINATION_STREET, DESTINATION_NUMBER))
            .price(PRICE)
            .build();
  }

  private TripPatchRequest createPatchRequest() {
    return TripPatchRequest.builder()
            .driverId(UPDATED_DRIVER_ID)
            .price(UPDATED_PRICE)
            .build();
  }

  private StatusUpdateRequest createStatusRequest() {
    return StatusUpdateRequest.builder()
            .status(TripStatus.IN_PROGRESS)
            .build();
  }

  private TripResponse createTripResponse() {
    return new TripResponse(
            TRIP_ID,
            DRIVER_ID,
            PASSENGER_ID,
            new Address(PICKUP_CITY, PICKUP_STREET, PICKUP_NUMBER),
            new Address(DESTINATION_CITY, DESTINATION_STREET, DESTINATION_NUMBER),
            TripStatus.CREATED,
            LocalDateTime.now(),
            PRICE
    );
  }

  private TripResponse createUpdatedTripResponse() {
    return new TripResponse(
            TRIP_ID,
            UPDATED_DRIVER_ID,
            PASSENGER_ID,
            new Address(PICKUP_CITY, PICKUP_STREET, PICKUP_NUMBER),
            new Address(DESTINATION_CITY, DESTINATION_STREET, DESTINATION_NUMBER),
            TripStatus.CREATED,
            LocalDateTime.now(),
            UPDATED_PRICE
    );
  }

  private TripResponse createStatusUpdatedResponse() {
    return new TripResponse(
            TRIP_ID,
            DRIVER_ID,
            PASSENGER_ID,
            new Address(PICKUP_CITY, PICKUP_STREET, PICKUP_NUMBER),
            new Address(DESTINATION_CITY, DESTINATION_STREET, DESTINATION_NUMBER),
            TripStatus.IN_PROGRESS,
            LocalDateTime.now(),
            PRICE
    );
  }


  @Test
  @DisplayName("POST should return 201 when trip created")
  public void shouldCreateTrip() {

    TripRequest request = createTripRequest();
    TripResponse response = createTripResponse();

    when(tripService.createTrip(any(TripRequest.class)))
            .thenReturn(response);

    assertDoesNotThrow(() -> mockMvc.perform(post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(TRIP_ID))
            .andExpect(jsonPath("$.passengerId").value(PASSENGER_ID))
            .andExpect(jsonPath("$.driverId").value(DRIVER_ID))

            .andExpect(jsonPath("$.pickupAddress.city").value(PICKUP_CITY))
            .andExpect(jsonPath("$.pickupAddress.street").value(PICKUP_STREET))
            .andExpect(jsonPath("$.pickupAddress.buildingNumber").value(PICKUP_NUMBER))

            .andExpect(jsonPath("$.destinationAddress.city").value(DESTINATION_CITY))
            .andExpect(jsonPath("$.destinationAddress.street").value(DESTINATION_STREET))
            .andExpect(jsonPath("$.destinationAddress.buildingNumber").value(DESTINATION_NUMBER))

            .andExpect(jsonPath("$.price").value(PRICE)));

    verify(tripService).createTrip(any(TripRequest.class));
  }

  @Test
  @DisplayName("POST should return 400 when request data is invalid")
  public void shouldReturnBadRequestWhenInvalidData() {
    TripRequest invalidRequest = TripRequest.builder().build();

    assertDoesNotThrow(() -> mockMvc.perform(post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(invalidRequest)))
            .andExpect(status().isBadRequest()));

    verify(tripService, never()).createTrip(any());
  }

  @Test
  @DisplayName("GET should return trip")
  public void shouldGetTrip() {

    TripResponse response = createTripResponse();

    when(tripService.getTripById(TRIP_ID)).thenReturn(response);

    assertDoesNotThrow(() -> mockMvc.perform(get(BASE_URL_WITH_ID, TRIP_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(TRIP_ID))
            .andExpect(jsonPath("$.passengerId").value(PASSENGER_ID))
            .andExpect(jsonPath("$.driverId").value(DRIVER_ID))

            .andExpect(jsonPath("$.pickupAddress.city").value(PICKUP_CITY))
            .andExpect(jsonPath("$.pickupAddress.street").value(PICKUP_STREET))
            .andExpect(jsonPath("$.pickupAddress.buildingNumber").value(PICKUP_NUMBER))

            .andExpect(jsonPath("$.destinationAddress.city").value(DESTINATION_CITY))
            .andExpect(jsonPath("$.destinationAddress.street").value(DESTINATION_STREET))
            .andExpect(jsonPath("$.destinationAddress.buildingNumber").value(DESTINATION_NUMBER))

            .andExpect(jsonPath("$.price").value(PRICE)));

    verify(tripService).getTripById(TRIP_ID);
  }

  @Test
  @DisplayName("GET should return 404 when trip not found")
  public void shouldReturnNotFoundWhenDriverMissing() {
    when(tripService.getTripById(TRIP_ID))
            .thenThrow(new TripNotFoundException(TRIP_ID));

    assertDoesNotThrow(() -> mockMvc.perform(get(BASE_URL_WITH_ID, TRIP_ID))
            .andExpect(status().isNotFound()));

    verify(tripService).getTripById(TRIP_ID);
  }


  @Test
  @DisplayName("PATCH should update trip")
  public void shouldPatchTrip() {

    TripPatchRequest patch = createPatchRequest();
    TripResponse response = createUpdatedTripResponse();

    when(tripService.patchTrip(eq(TRIP_ID), any()))
            .thenReturn(response);

    assertDoesNotThrow(() -> mockMvc.perform(patch(BASE_URL_WITH_ID, TRIP_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(patch)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.driverId").value(UPDATED_DRIVER_ID))
            .andExpect(jsonPath("$.price").value(UPDATED_PRICE)));

    verify(tripService).patchTrip(eq(TRIP_ID), any());
  }

  @Test
  @DisplayName("PATCH status should update trip status")
  void shouldUpdateTripStatus() {

    StatusUpdateRequest request = createStatusRequest();
    TripResponse response = createStatusUpdatedResponse();

    when(tripService.updateTripStatus(eq(TRIP_ID), any()))
            .thenReturn(response);

    assertDoesNotThrow(() -> mockMvc.perform(patch(STATUS_URL, TRIP_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("IN_PROGRESS")));

    verify(tripService).updateTripStatus(eq(TRIP_ID), any());
  }

  @Test
  @DisplayName("PATCH should return 404 when trip not found")
  public void shouldReturnNotFoundWhenPatching() {
    TripPatchRequest patchRequest = TripPatchRequest.builder()
            .driverId(UPDATED_DRIVER_ID)
            .build();

    when(tripService.patchTrip(eq(DRIVER_ID), any(TripPatchRequest.class)))
            .thenThrow(new TripNotFoundException(DRIVER_ID));

    assertDoesNotThrow(() -> mockMvc.perform(patch(BASE_URL_WITH_ID, DRIVER_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(patchRequest)))
            .andExpect(status().isNotFound()));
  }


  @Test
  @DisplayName("DELETE should return 204 when trip deleted successfully")
  public void shouldDeleteTrip() {
    doNothing().when(tripService).deleteTrip(DRIVER_ID);

    assertDoesNotThrow(() -> mockMvc.perform(delete(BASE_URL_WITH_ID, DRIVER_ID))
            .andExpect(status().isNoContent()));

    verify(tripService).deleteTrip(DRIVER_ID);
  }

  @Test
  @DisplayName("DELETE should return 404 when trip not found")
  public void shouldReturnNotFoundWhenDeleting() {
    doThrow(new TripNotFoundException(DRIVER_ID))
            .when(tripService).deleteTrip(DRIVER_ID);

    assertDoesNotThrow(() -> mockMvc.perform(delete(BASE_URL_WITH_ID, DRIVER_ID))
            .andExpect(status().isNotFound()));
  }

}






