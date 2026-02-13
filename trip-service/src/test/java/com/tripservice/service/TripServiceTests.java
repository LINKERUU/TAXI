package com.tripservice.service;

import com.tripservice.client.grpc.DriverGrpcClient;
import com.tripservice.client.grpc.PassengerGrpcClient;
import com.tripservice.dto.StatusUpdateRequest;
import com.tripservice.dto.TripRequest;
import com.tripservice.dto.TripResponse;
import com.tripservice.mapper.TripMapper;
import com.tripservice.model.Address;
import com.tripservice.model.Trip;
import com.tripservice.model.enums.TripStatus;
import com.tripservice.repository.TripRepository;
import com.tripservice.service.impl.TripServiceImpl;
import com.tripservice.service.producer.TripProducerEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TripServiceTests {

  @InjectMocks
  private TripServiceImpl tripService;

  @Mock
  private TripRepository tripRepository;

  @Mock
  private PassengerGrpcClient passengerGrpcClient;

  @Mock
  private DriverGrpcClient driverGrpcClient;

  @Mock
  private TripProducerEvent tripProducerEvent;

  @Mock
  private TripMapper tripMapper;

  private Trip trip;
  private TripRequest tripRequest;
  private TripResponse tripResponse;
  private StatusUpdateRequest statusUpdateRequest;

  @BeforeEach
  public void setUp() {

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
    private Address createAddress(String city,String street,String number) {
      return Address.builder()
              .city(city)
              .street(street)
              .buildingNumber(number)
              .build();
  }

  @Test
  void createTrip_Success() {

    when(passengerGrpcClient.validatePassenger(anyLong())).thenReturn(true);
    when(driverGrpcClient.validateDriver(anyLong())).thenReturn(true);
    when(tripMapper.toEntity(any(TripRequest.class))).thenReturn(trip);
    when(tripRepository.save(any(Trip.class))).thenReturn(trip);
    when(tripMapper.toResponse(any(Trip.class))).thenReturn(tripResponse);

    TripResponse result = tripService.createTrip(tripRequest);

    assertNotNull(result);
    assertEquals(1L,result.getId());
    assertEquals(1L, result.getDriverId());
    assertEquals(2L, result.getPassengerId());
    assertEquals(BigDecimal.valueOf(25.20), result.getPrice());
    assertEquals(TripStatus.CREATED,result.getStatus());

    verify(passengerGrpcClient).validatePassenger(2L);
    verify(driverGrpcClient).validateDriver(1L);
    verify(tripMapper).toEntity(tripRequest);
    verify(tripRepository).save(trip);
    verify(tripMapper).toResponse(trip);

  }

  @Test
  void createTrip_InvalidPassenger_ThrowsException() {
    when(passengerGrpcClient.validatePassenger(anyLong())).thenReturn(false);

    assertThatThrownBy(() -> tripService.createTrip(tripRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid passenger ID: 2");

    verify(passengerGrpcClient).validatePassenger(2L);
    verify(driverGrpcClient, never()).validateDriver(anyLong());
    verify(tripRepository, never()).save(any());
  }

  @Test
  void createTrip_InvalidDriver_ThrowsException() {

    when(passengerGrpcClient.validatePassenger(anyLong())).thenReturn(true);
    when(driverGrpcClient.validateDriver(anyLong())).thenReturn(false);

    assertThatThrownBy(() -> tripService.createTrip(tripRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid driver ID: 1");

    verify(passengerGrpcClient).validatePassenger(2L);
    verify(driverGrpcClient).validateDriver(1L);
    verify(tripRepository, never()).save(any());
  }

  @Test
  void createTripFallback_ThrowsException() {

    Throwable cause = new RuntimeException("gRPC service unavailable");
    
    assertThatThrownBy(() -> tripService.createTripFallback(tripRequest, cause))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot create trip at the moment")
            .hasMessageContaining("gRPC service unavailable");
  }

  @Test
  void getTripById_Success() {
    when(tripRepository.findById(1L)).thenReturn(trip);
    when(tripMapper.toResponse(trip)).thenReturn(tripResponse);

    TripResponse result = tripService.getTripById(1L);
    assertNotNull(result);
    assertEquals(1L,result.getId());
    verify(tripRepository).findById(1L);
    verify(tripMapper).toResponse(trip);
  }

  @Test
  void getTripByIdFallback_ThrowsException() {

    Throwable cause = new RuntimeException("gRPC service unavailable");
    Long tripId = 1L;

    Trip trip = new Trip();
    when(tripRepository.findById(tripId)).thenReturn(trip);

    TripResponse expectedResponse = TripResponse.builder().build();
    when(tripMapper.toFallbackResponse(trip)).thenReturn(expectedResponse);

    TripResponse result = tripService.getTripByIdFallback(tripId, cause);

    assertNotNull(result);
    verify(tripRepository).findById(tripId);
    verify(tripMapper).toFallbackResponse(trip);
  }

  @Test
  void updateTrip_Success() {

    TripRequest updateRequest = TripRequest.builder()
            .driverId(1L)
            .passengerId(2L)
            .pickupAddress("Минск, Немига, 5")
            .destinationAddress("Минск, Притыцкого, 30")
            .price(BigDecimal.valueOf(35.00))
            .build();

    Trip updatedTrip = Trip.builder()
            .id(1L)
            .driverId(1L)
            .passengerId(2L)
            .pickupAddress(createAddress("Минск", "Немига", "5"))
            .destinationAddress(createAddress("Минск", "Притыцкого", "30"))
            .status(TripStatus.CREATED)
            .price(BigDecimal.valueOf(35.00))
            .build();

    TripResponse updatedResponse = TripResponse.builder()
            .id(1L)
            .driverId(1L)
            .passengerId(2L)
            .pickupAddress("Минск, Немига, 5")
            .destinationAddress("Минск, Притыцкого, 30")
            .status(TripStatus.CREATED)
            .price(BigDecimal.valueOf(35.00))
            .build();

    when(tripRepository.findById(1L)).thenReturn(trip);
    when(passengerGrpcClient.validatePassenger(anyLong())).thenReturn(true);
    when(driverGrpcClient.validateDriver(anyLong())).thenReturn(true);
    doNothing().when(tripMapper).updateEntityFromRequest(any(TripRequest.class), any(Trip.class));
    when(tripRepository.save(any(Trip.class))).thenReturn(updatedTrip);
    when(tripMapper.toResponse(any(Trip.class))).thenReturn(updatedResponse);

    TripResponse result = tripService.updateTrip(1L, updateRequest);

    assertNotNull(result);
    assertEquals("Минск, Немига, 5", result.getPickupAddress());
    assertEquals(BigDecimal.valueOf(35.00), result.getPrice());

    verify(tripRepository).findById(1L);
    verify(tripMapper).updateEntityFromRequest(updateRequest, trip);
    verify(tripRepository).save(trip);
  }

  @Test
  void updateTripFallback_ThrowsException() {

    Throwable cause = new RuntimeException("gRPC service unavailable");

    assertThatThrownBy(() -> tripService.updateTripFallback(1L,tripRequest, cause))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot update trip at the moment")
            .hasMessageContaining("gRPC service unavailable");
  }

  @Test
  void deleteTrip_Success() {
    when(tripRepository.existsById(1L)).thenReturn(true);
    doNothing().when(tripRepository).deleteById(1L);

    tripService.deleteTrip(1L);

    verify(tripRepository).existsById(1L);
    verify(tripRepository).deleteById(1L);
  }

  @Test
  void updateTripStatus_CreatedToAccepted_Success() {

    statusUpdateRequest.setStatus(TripStatus.ACCEPTED);

    Trip updatedTrip = Trip.builder()
            .id(1L)
            .status(TripStatus.ACCEPTED)
            .build();

    TripResponse updatedResponse = TripResponse.builder()
            .id(1L)
            .status(TripStatus.ACCEPTED)
            .build();

    when(tripRepository.findById(1L)).thenReturn(trip);
    when(tripRepository.save(any(Trip.class))).thenReturn(updatedTrip);
    when(tripMapper.toResponse(any(Trip.class))).thenReturn(updatedResponse);

    TripResponse result = tripService.updateTripStatus(1L, statusUpdateRequest);

    assertNotNull(result);
    assertEquals(TripStatus.ACCEPTED, result.getStatus());
    verify(tripProducerEvent, never()).sendTripCompletedEvent(anyLong(), anyLong(), anyLong());
    verify(tripRepository).save(trip);
  }

  @Test
  void updateTripStatus_ToCompleted_SendsEvent() {

    trip.setStatus(TripStatus.IN_PROGRESS);
    statusUpdateRequest.setStatus(TripStatus.COMPLETED);

    Trip updatedTrip = Trip.builder()
            .id(1L)
            .driverId(1L)
            .passengerId(2L)
            .status(TripStatus.COMPLETED)
            .build();

    TripResponse updatedResponse = TripResponse.builder()
            .id(1L)
            .status(TripStatus.COMPLETED)
            .build();

    when(tripRepository.findById(1L)).thenReturn(trip);
    when(tripRepository.save(any(Trip.class))).thenReturn(updatedTrip);
    when(tripMapper.toResponse(any(Trip.class))).thenReturn(updatedResponse);
    doNothing().when(tripProducerEvent).sendTripCompletedEvent(1L, 1L, 2L);

    TripResponse result = tripService.updateTripStatus(1L, statusUpdateRequest);

    assertEquals(TripStatus.COMPLETED, result.getStatus());
    verify(tripProducerEvent).sendTripCompletedEvent(1L, 1L, 2L);
    verify(tripRepository).save(trip);
  }

  @Test
  void updateTripStatusFallback_ProcessesStatusUpdate() {
    trip.setStatus(TripStatus.CREATED);
    statusUpdateRequest.setStatus(TripStatus.ACCEPTED);
    Throwable cause = new RuntimeException("Circuit breaker open");

    when(tripRepository.findById(1L)).thenReturn(trip);
    when(tripRepository.save(any(Trip.class))).thenReturn(trip);
    when(tripMapper.toFallbackResponse(any(Trip.class))).thenReturn(tripResponse);

    TripResponse result = tripService.updateTripStatusFallback(1L, statusUpdateRequest,cause);
    assertNotNull(result);
    verify(tripRepository).save(trip);
    verify(tripProducerEvent, never()).sendTripCompletedEvent(anyLong(), anyLong(), anyLong());
  }

  @ParameterizedTest
  @CsvSource({
          "CREATED, ACCEPTED, true",
          "CREATED, CANCELLED, true",
          "CREATED, DRIVER_EN_ROUTE, false",
          "CREATED, COMPLETED, false",

          "ACCEPTED, DRIVER_EN_ROUTE, true",
          "ACCEPTED, CANCELLED, true",
          "ACCEPTED, CREATED, false",
          "ACCEPTED, COMPLETED, false",

          "DRIVER_EN_ROUTE, PASSENGER_ON_BOARD, true",
          "DRIVER_EN_ROUTE, CANCELLED, true",
          "DRIVER_EN_ROUTE, ACCEPTED, false",
          "DRIVER_EN_ROUTE, COMPLETED, false",

          "PASSENGER_ON_BOARD, IN_PROGRESS, true",
          "PASSENGER_ON_BOARD, CANCELLED, true",
          "PASSENGER_ON_BOARD, DRIVER_EN_ROUTE, false",
          "PASSENGER_ON_BOARD, COMPLETED, false",

          "IN_PROGRESS, COMPLETED, true",
          "IN_PROGRESS, CANCELLED, true",
          "IN_PROGRESS, PASSENGER_ON_BOARD, false",
          "IN_PROGRESS, ACCEPTED, false",

          "COMPLETED, CANCELLED, false",
          "COMPLETED, IN_PROGRESS, false",
          "CANCELLED, CREATED, false",
          "CANCELLED, COMPLETED, false"
  })
  void validateStatusTransition_ShouldValidateCorrectly(
          TripStatus from, TripStatus to, boolean shouldBeValid) {

    Trip trip = Trip.builder()
            .id(1L)
            .status(from)
            .build();

    StatusUpdateRequest request = StatusUpdateRequest.builder()
            .status(to)
            .build();

    when(tripRepository.findById(1L)).thenReturn(trip);

    if (shouldBeValid) {

      Trip updatedTrip = Trip.builder()
              .id(1L)
              .status(to)
              .build();
      when(tripRepository.save(any(Trip.class))).thenReturn(updatedTrip);
      when(tripMapper.toResponse(any(Trip.class))).thenReturn(mock(TripResponse.class));

      assertDoesNotThrow(() -> tripService.updateTripStatus(1L, request));
      verify(tripRepository).save(any(Trip.class));
    } else {
      assertThrows(IllegalArgumentException.class,
              () -> tripService.updateTripStatus(1L, request));
      verify(tripRepository, never()).save(any(Trip.class));
    }
  }

}


