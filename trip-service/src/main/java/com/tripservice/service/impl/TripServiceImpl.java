package com.tripservice.service.impl;

import com.tripservice.client.grpc.DriverGrpcClient;
import com.tripservice.client.grpc.PassengerGrpcClient;
import com.tripservice.dto.StatusUpdateRequest;
import com.tripservice.dto.TripPatchRequest;
import com.tripservice.dto.TripRequest;
import com.tripservice.dto.TripResponse;
import com.tripservice.dto.event.TripCompletedEvent;
import com.tripservice.exception.custom.InvalidTransitionStatusException;
import com.tripservice.exception.custom.TripNotFoundException;
import com.tripservice.mapper.TripMapper;
import com.tripservice.model.Address;
import com.tripservice.model.Trip;
import com.tripservice.model.enums.TripStatus;
import com.tripservice.repository.TripRepository;
import com.tripservice.service.TripService;
import com.tripservice.service.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

  private final TripRepository tripRepository;
  private final TripMapper tripMapper;
  private final PassengerGrpcClient passengerGrpcClient;
  private final DriverGrpcClient driverGrpcClient;
  private final OutboxService outboxService;

  private static final String completedEvent = "trip-completed-event";

  @Override
  @Transactional
  public TripResponse createTrip(TripRequest request) {

    driverGrpcClient.existsDriver(request.getDriverId());
    passengerGrpcClient.existsPassenger(request.getPassengerId());

    Trip trip = tripMapper.toEntity(request);
    tripRepository.save(trip);

    log.info("Trip created with ID: {}", trip.getId());

    return tripMapper.toResponse(trip);
  }

  @Override
  public TripResponse getTripById(Long id) {
    log.info("Getting trip with ID: {}", id);
    return tripMapper.toResponse(getExistsTrip(id));
  }

  @Override
  @Transactional
  public TripResponse patchTrip(Long id, TripPatchRequest request) {

    Trip trip = getExistsTrip(id);

    applyPatch(trip, request);

    log.info("Trip with ID {} updated", id);

    return tripMapper.toResponse(trip);
  }

  @Override
  @Transactional
  public void deleteTrip(Long id) {
    log.info("Deleting trip with ID: {}", id);

    getExistsTrip(id);

    tripRepository.deleteById(id);
  }

  //переделать в outbox паттерн
  @Override
  @Transactional
  public TripResponse updateTripStatus(Long id, StatusUpdateRequest request) {

    TripStatus newStatus = request.getStatus();

    log.info("Updating trip status to {} for trip ID: {}", newStatus, id);

    Trip trip = getExistsTrip(id);

    checkTransitionStatus(trip.getStatus(), newStatus);

    trip.changeStatus(newStatus);

    if (newStatus == TripStatus.COMPLETED) {
      TripCompletedEvent event = tripMapper.toCompletedEvent(trip);
      outboxService.saveEvent(event, completedEvent);
    }

    return tripMapper.toResponse(trip);
  }


  private void checkTransitionStatus(TripStatus current, TripStatus next) {
    if (!TripStatus.canTransitionTo(current, next)) {
      throw new InvalidTransitionStatusException(current, next);
    }
  }

  private Trip getExistsTrip(Long id) {
    return tripRepository.findById(id).orElseThrow(() -> new TripNotFoundException(id));
  }

  private void applyPatch(Trip trip, TripPatchRequest request) {
    Optional.ofNullable(request.getDriverId()).ifPresent(driverId -> {
      driverGrpcClient.existsDriver(driverId);
      trip.changeDriverId(driverId);
    });
    Optional.ofNullable(request.getPickupAddress()).ifPresent(pickupAddress->{
      Address address = tripMapper.toAddress(pickupAddress);
      trip.changePickupAddress(address);
    });
    Optional.ofNullable(request.getDestinationAddress()).ifPresent(destinationAddress-> {
      Address address = tripMapper.toAddress(destinationAddress);
      trip.changeDestinationAddress(address);
    });
    Optional.ofNullable(request.getPrice()).ifPresent(trip::changePrice);
  }
}