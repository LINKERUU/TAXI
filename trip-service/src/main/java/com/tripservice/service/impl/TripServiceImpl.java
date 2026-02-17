package com.tripservice.service.impl;

<<<<<<< Updated upstream
=======
import com.tripservice.client.grpc.DriverGrpcClient;
import com.tripservice.client.grpc.PassengerGrpcClient;
>>>>>>> Stashed changes
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

    var trip = tripMapper.toEntity(request);
    var savedTrip = tripRepository.save(trip);

    log.info("Trip created with ID: {}", savedTrip.getId());
    return tripMapper.toResponse(savedTrip);
  }

  @Override
  public TripResponse getTripById(Long id) {
    log.debug("Fetching trip with ID: {}", id);

    var trip = tripRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Trip not found with id: " + id));
    return tripMapper.toResponse(trip);
  }

<<<<<<< Updated upstream
=======
  public TripResponse getTripByIdFallback(Long id, Throwable e) {
    log.warn("Circuit Breaker fallback for getTripById: {}. Error: {}", id, e.getMessage());

    var trip = tripRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Trip not found with id: " + id));

    return tripMapper.toFallbackResponse(trip);
  }

>>>>>>> Stashed changes
  @Override
  @Transactional
  public TripResponse updateTrip(Long id, TripRequest request) {
    log.info("Updating trip with ID: {}", id);

    var trip = tripRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Trip not found with id: " + id));

    // Используем Feign Clients для валидации - ЗАМЕНИ ЭТУ ЧАСТЬ!
    externalValidationService.validateDriver(request.getDriverId());
    externalValidationService.validatePassenger(request.getPassengerId());

    tripMapper.updateEntityFromRequest(request,trip);
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

    var trip = tripRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Trip not found with id: " + id));

    validateStatusTransition(trip.getStatus(), request.getStatus());

    trip.setStatus(request.getStatus());
    Trip updatedTrip = tripRepository.save(trip);

    return tripMapper.toResponse(updatedTrip);
  }

<<<<<<< Updated upstream
  // Удали старый метод validateDriverAndPassenger - ОН НЕ НУЖЕН!
  // private void validateDriverAndPassenger(Long driverId, Long passengerId) {
  //     ...
  // }
=======
  public TripResponse updateTripStatusFallback(Long id, StatusUpdateRequest request, Throwable e) {
    log.warn("Circuit Breaker fallback for updateTripStatus. ID: {}, Status: {}. Error: {}",
            id, request.getStatus(), e.getMessage());

    Trip trip = tripRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Trip not found with id: " + id));

    validateStatusTransition(trip.getStatus(), request.getStatus());

    trip.setStatus(request.getStatus());
    Trip updatedTrip = tripRepository.save(trip);

    return tripMapper.toFallbackResponse(updatedTrip);
  }
>>>>>>> Stashed changes

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

    if (current == TripStatus.DRIVER_EN_ROUTE &&
            !(next == TripStatus.PASSENGER_ON_BOARD || next == TripStatus.CANCELLED)) {
      throw new IllegalArgumentException(
              String.format("Cannot change status from %s to %s", current, next)
      );
    }
    if (current == TripStatus.PASSENGER_ON_BOARD &&
            !(next == TripStatus.IN_PROGRESS || next == TripStatus.CANCELLED)) {
      throw new IllegalArgumentException(
              String.format("Cannot change status from %s to %s", current, next)
      );

    }
    if (current == TripStatus.IN_PROGRESS &&
            !(next == TripStatus.COMPLETED || next == TripStatus.CANCELLED)) {
      throw new IllegalArgumentException(
              String.format("Cannot change status from %s to %s", current, next)
      );
    }
  }
}