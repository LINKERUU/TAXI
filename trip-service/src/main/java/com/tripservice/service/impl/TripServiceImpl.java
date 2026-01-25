package com.tripservice.service.impl;

import com.tripservice.dto.StatusUpdateRequest;
import com.tripservice.dto.TripRequest;
import com.tripservice.dto.TripResponse;
import com.tripservice.mapper.TripMapper;
import com.tripservice.model.Trip;
import com.tripservice.model.enums.TripStatus;
import com.tripservice.repository.TripRepository;
import com.tripservice.service.TripService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TripServiceImpl implements TripService {

  private final TripRepository tripRepository;
  private final TripMapper tripMapper;
  private final ExternalValidationService externalValidationService; // ДОБАВЬ ЭТУ СТРОКУ!

  @Override
  @Transactional
  public TripResponse createTrip(TripRequest request) {
    log.info("Creating trip for driver {} and passenger {}",
            request.getDriverId(), request.getPassengerId());

    // Используем Feign Clients для валидации - ЗАМЕНИ ЭТУ ЧАСТЬ!
    externalValidationService.validateDriver(request.getDriverId());
    externalValidationService.validatePassenger(request.getPassengerId());

    Trip trip = tripMapper.toEntity(request);
    Trip savedTrip = tripRepository.save(trip);

    log.info("Trip created with ID: {}", savedTrip.getId());
    return tripMapper.toResponse(savedTrip);
  }

  @Override
  public TripResponse getTripById(Long id) {
    log.debug("Fetching trip with ID: {}", id);

    Trip trip = tripRepository.findById(id);
    return tripMapper.toResponse(trip);
  }

  @Override
  @Transactional
  public TripResponse updateTrip(Long id, TripRequest request) {
    log.info("Updating trip with ID: {}", id);

    Trip trip = tripRepository.findById(id);

    // Используем Feign Clients для валидации - ЗАМЕНИ ЭТУ ЧАСТЬ!
    externalValidationService.validateDriver(request.getDriverId());
    externalValidationService.validatePassenger(request.getPassengerId());

    tripMapper.updateEntityFromRequest(request, trip);
    Trip updatedTrip = tripRepository.save(trip);

    return tripMapper.toResponse(updatedTrip);
  }

  @Override
  @Transactional
  public void deleteTrip(Long id) {
    log.info("Deleting trip with ID: {}", id);

    if (!tripRepository.existsById(id)) {
      throw new RuntimeException("Trip not found with id: " + id);
    }

    tripRepository.deleteById(id);
  }

  @Override
  @Transactional
  public TripResponse updateTripStatus(Long id, StatusUpdateRequest request) {
    log.info("Updating trip status to {} for trip ID: {}", request.getStatus(), id);

    Trip trip = tripRepository.findById(id);

    validateStatusTransition(trip.getStatus(), request.getStatus());

    trip.setStatus(request.getStatus());
    Trip updatedTrip = tripRepository.save(trip);

    return tripMapper.toResponse(updatedTrip);
  }

  // Удали старый метод validateDriverAndPassenger - ОН НЕ НУЖЕН!
  // private void validateDriverAndPassenger(Long driverId, Long passengerId) {
  //     ...
  // }

  private void validateStatusTransition(TripStatus current, TripStatus next) {
    // ... остальной код без изменений
    if (current == TripStatus.COMPLETED || current == TripStatus.CANCELLED) {
      throw new IllegalArgumentException(
              String.format("Cannot change status from %s to %s", current, next)
      );
    }

    if (current == TripStatus.CREATED &&
            !(next == TripStatus.ACCEPTED || next == TripStatus.CANCELLED)) {
      throw new IllegalArgumentException(
              String.format("Cannot change status from %s to %s", current, next)
      );
    }

    if (current == TripStatus.ACCEPTED &&
            !(next == TripStatus.DRIVER_EN_ROUTE || next == TripStatus.CANCELLED)) {
      throw new IllegalArgumentException(
              String.format("Cannot change status from %s to %s", current, next)
      );
    }
  }
}