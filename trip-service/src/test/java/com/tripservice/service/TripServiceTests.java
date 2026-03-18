package com.tripservice.service;

import com.tripservice.client.grpc.DriverGrpcClient;
import com.tripservice.client.grpc.PassengerGrpcClient;
import com.tripservice.dto.*;
import com.tripservice.dto.event.TripCompletedEvent;
import com.tripservice.exception.custom.InvalidTransitionStatusException;
import com.tripservice.exception.custom.TripNotFoundException;
import com.tripservice.mapper.TripMapper;
import com.tripservice.model.Address;
import com.tripservice.model.Trip;
import com.tripservice.model.enums.TripStatus;
import com.tripservice.repository.TripRepository;
import com.tripservice.service.impl.TripServiceImpl;
import com.tripservice.service.outbox.OutboxService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TripServiceTests {

  private static final Long TRIP_ID = 1L;
  private static final Long NON_EXISTENT_ID = 999L;
  private static final Long DRIVER_ID = 1L;
  private static final Long PASSENGER_ID = 1L;

  private static final String PICKUP_CITY = "Минск";
  private static final String PICKUP_STREET = "Л.Беды";
  private static final String PICKUP_NUMBER = "4";
  private static final String DESTINATION_CITY = "Минск";
  private static final String DESTINATION_STREET = "Ленина";
  private static final String DESTINATION_NUMBER = "7";

  private static final BigDecimal PRICE = BigDecimal.valueOf(10.84);
  private static final BigDecimal UPDATE_PRICE = BigDecimal.valueOf(15.84);
  private static final Long UPDATED_DRIVER_ID = 2L;
  private static final String UPDATED_PICKUP_CITY = "Минск";
  private static final String UPDATED_PICKUP_STREET = "Комсомольская";
  private static final String UPDATED_PICKUP_NUMBER = "26";
  private static final String UPDATED_DESTINATION_CITY = "Минск";
  private static final String UPDATED_DESTINATION_STREET = "Пушкина";
  private static final String UPDATED_DESTINATION_NUMBER = "77";

  @Mock
  private TripRepository tripRepository;

  @Mock
  private TripMapper tripMapper;

  @Mock
  private PassengerGrpcClient passengerGrpcClient;

  @Mock
  private DriverGrpcClient driverGrpcClient;

  @Mock
  private OutboxService outboxService;

  @InjectMocks
  private TripServiceImpl tripService;

  private Trip trip;
  private TripRequest tripRequest;
  private TripResponse tripResponse;

  @BeforeEach
  public void setUp() {
    trip = new Trip(
            DRIVER_ID,
            PASSENGER_ID,
            new Address(PICKUP_CITY, PICKUP_STREET, PICKUP_NUMBER),
            new Address(DESTINATION_CITY, DESTINATION_STREET, DESTINATION_NUMBER),
            PRICE
    );

    tripRequest = TripRequest.builder()
            .driverId(DRIVER_ID)
            .passengerId(PASSENGER_ID)
            .pickupAddress(new AddressRequest(PICKUP_CITY, PICKUP_STREET, PICKUP_NUMBER))
            .destinationAddress(new AddressRequest(DESTINATION_CITY, DESTINATION_STREET, DESTINATION_NUMBER))
            .price(PRICE)
            .build();

    tripResponse = new TripResponse(
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

  @Test
  @DisplayName("Should create trip successfully")
  public void createTripSuccess() {

    when(tripMapper.toEntity(tripRequest)).thenReturn(trip);
    when(tripRepository.save(trip)).thenReturn(trip);
    when(tripMapper.toResponse(trip)).thenReturn(tripResponse);

    TripResponse result = tripService.createTrip(tripRequest);

    assertNotNull(result);
    assertEquals(TRIP_ID, result.id());

    verify(driverGrpcClient).existsDriver(DRIVER_ID);
    verify(passengerGrpcClient).existsPassenger(PASSENGER_ID);
    verify(tripRepository).save(trip);
    verify(tripMapper).toResponse(trip);
  }

  @Test
  @DisplayName("Should return trip when id exists")
  public void getTripByIdSuccess() {

    when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.of(trip));
    when(tripMapper.toResponse(trip)).thenReturn(tripResponse);

    TripResponse result = tripService.getTripById(TRIP_ID);

    assertNotNull(result);
    assertEquals(TRIP_ID, result.id());

    verify(tripRepository).findById(TRIP_ID);
    verify(tripMapper).toResponse(trip);
  }

  @Test
  @DisplayName("Should throw TripNotFoundException when trip not found")
  public void getTripByIdThrowsException() {

    when(tripRepository.findById(NON_EXISTENT_ID)).thenReturn(Optional.empty());

    assertThrows(TripNotFoundException.class,
            () -> tripService.getTripById(NON_EXISTENT_ID));

    verify(tripRepository).findById(NON_EXISTENT_ID);
  }

  @Test
  @DisplayName("Should patch trip successfully")
  public void patchTripSuccess() {

    TripPatchRequest patchRequest = TripPatchRequest.builder()
            .driverId(UPDATED_DRIVER_ID)
            .price(UPDATE_PRICE)
            .destinationAddress(
                    new AddressRequest(
                            UPDATED_DESTINATION_CITY,
                            UPDATED_DESTINATION_STREET,
                            UPDATED_DESTINATION_NUMBER))
            .pickupAddress(
                    new AddressRequest(
                            UPDATED_PICKUP_CITY,
                            UPDATED_PICKUP_STREET,
                            UPDATED_PICKUP_NUMBER)
            )
            .build();

    when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.of(trip));
    when(tripMapper.toResponse(trip)).thenReturn(tripResponse);

    TripResponse result = tripService.patchTrip(TRIP_ID, patchRequest);

    assertNotNull(result);

    verify(tripRepository).findById(TRIP_ID);
    verify(tripMapper).toResponse(trip);
  }

  @Test
  @DisplayName("Should patch trip status completed")
  public void patchTripStatusCompleted() {

    TripPatchRequest patchRequest = TripPatchRequest.builder()
            .driverId(UPDATED_DRIVER_ID)
            .price(UPDATE_PRICE)
            .destinationAddress(
                    new AddressRequest(
                            UPDATED_DESTINATION_CITY,
                            UPDATED_DESTINATION_STREET,
                            UPDATED_DESTINATION_NUMBER))
            .pickupAddress(
                    new AddressRequest(
                            UPDATED_PICKUP_CITY,
                            UPDATED_PICKUP_STREET,
                            UPDATED_PICKUP_NUMBER)
            )
            .build();

    when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.of(trip));
    when(tripMapper.toResponse(trip)).thenReturn(tripResponse);

    TripResponse result = tripService.patchTrip(TRIP_ID, patchRequest);

    assertNotNull(result);

    verify(tripRepository).findById(TRIP_ID);
    verify(tripMapper).toResponse(trip);
  }

  @Test
  @DisplayName("Should send TripCompletedEvent when trip status updated to COMPLETED")
  public void updateTripStatusCompletedSendsEvent() {

    trip.changeStatus(TripStatus.IN_PROGRESS);
    StatusUpdateRequest request = StatusUpdateRequest.builder()
            .status(TripStatus.COMPLETED)
            .build();

    TripCompletedEvent event = mock(TripCompletedEvent.class);

    when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.of(trip));
    when(tripMapper.toCompletedEvent(trip)).thenReturn(event);
    when(tripMapper.toResponse(trip)).thenReturn(tripResponse);

    TripResponse result = tripService.updateTripStatus(TRIP_ID, request);

    assertNotNull(result);
    verify(tripMapper).toCompletedEvent(trip);
    verify(outboxService).saveEvent(event, "trip-completed-event");
    verify(tripMapper).toResponse(trip);
  }

  @Test
  @DisplayName("Should throw InvalidTransitionStatusException on invalid status transition")
  public void updateTripStatusInvalidTransition() {
    trip.changeStatus(TripStatus.CREATED);
    StatusUpdateRequest request = StatusUpdateRequest.builder()
            .status(TripStatus.COMPLETED)
            .build();

    when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.of(trip));

    assertThrows(InvalidTransitionStatusException.class,
            () -> tripService.updateTripStatus(TRIP_ID, request));
  }

  @Test
  @DisplayName("Should delete trip when id exists")
  public void deleteTripSuccess() {

    when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.of(trip));

    tripService.deleteTrip(TRIP_ID);

    verify(tripRepository).deleteById(TRIP_ID);
  }

  @Test
  @DisplayName("Should update trip status")
  public void updateTripStatusSuccess() {

    StatusUpdateRequest request = StatusUpdateRequest.builder()
            .status(TripStatus.ACCEPTED)
            .build();

    when(tripRepository.findById(TRIP_ID)).thenReturn(Optional.of(trip));
    when(tripMapper.toResponse(trip)).thenReturn(tripResponse);

    TripResponse result = tripService.updateTripStatus(TRIP_ID, request);

    assertNotNull(result);

    verify(tripRepository).findById(TRIP_ID);
    verify(tripMapper).toResponse(trip);
  }
}


