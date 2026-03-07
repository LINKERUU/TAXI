package com.passengerservice.service.impl;

import com.passengerservice.dto.PassengerRequest;
import com.passengerservice.dto.PassengerResponse;
import com.passengerservice.mapper.PassengerMapper;
import com.passengerservice.model.Passenger;
import com.passengerservice.repository.PassengerRepository;
import com.passengerservice.service.PassengerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

  private final PassengerRepository passengerRepository;
  private final PassengerMapper passengerMapper;

  @Override
  @Transactional
  public PassengerResponse createPassenger(PassengerRequest request) {
    log.info("Creating passenger with email: {}", request.getEmail());


    var passenger = passengerMapper.toEntity(request);

    Passenger savedPassenger = passengerRepository.save(passenger);
    log.info("Passenger created with ID: {}", savedPassenger.getId());

    return passengerMapper.toPassengerResponse(savedPassenger);
  }

  @Override
  @Transactional(readOnly = true)
  public PassengerResponse getPassengerById(Long id) {
    log.info("Getting passenger with ID: {}", id);
    var passenger = passengerRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new  RuntimeException("Passenger not found" + id));
    return passengerMapper.toPassengerResponse(passenger);
  }

  @Override
  @Transactional
  public PassengerResponse updatePassenger(Long id, PassengerRequest request) throws Exception {
    log.info("Updating passenger with ID: {}", id);

    Passenger existingPassenger = passengerRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new Exception("Passenger not found with id: " + id));

    passengerMapper.updatePassengerFromRequest(request, existingPassenger);

    Passenger updatedPassenger = passengerRepository.save(existingPassenger);
    log.info("Passenger with ID {} updated", id);

    return passengerMapper.toPassengerResponse(updatedPassenger);
  }

  @Override
  @Transactional
  public void deletePassenger(Long id) throws Exception {
    log.info("Soft deleting passenger with ID: {}", id);

    Passenger passenger = passengerRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new Exception("Passenger not found with id: " + id));

    passenger.setDeleted(true);
    passengerRepository.save(passenger);
    log.info("Passenger with ID {} soft deleted", id);
  }
}