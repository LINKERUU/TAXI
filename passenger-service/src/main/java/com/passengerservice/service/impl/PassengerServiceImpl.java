package com.passengerservice.service.impl;

import com.passengerservice.dto.PassengerPatchRequest;
import com.passengerservice.dto.PassengerRequest;
import com.passengerservice.dto.PassengerResponse;
import com.passengerservice.exception.custom.DuplicateEmailException;
import com.passengerservice.exception.custom.DuplicatePhoneException;
import com.passengerservice.exception.custom.PassengerNotFoundException;
import com.passengerservice.mapper.PassengerMapper;
import com.passengerservice.model.Passenger;
import com.passengerservice.repository.PassengerRepository;
import com.passengerservice.service.PassengerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

    validateEmailNotExists(request.getEmail());
    validatePhoneNotExists(request.getPhone());

    Passenger passenger = passengerMapper.toEntity(request);
    passengerRepository.save(passenger);

    log.info("Passenger created successfully");

    return passengerMapper.toPassengerResponse(passenger);
  }

  @Override
  public PassengerResponse getPassengerById(Long id) {
    log.info("Getting passenger with ID: {}", id);
    return passengerMapper.toPassengerResponse(getExistsPassenger(id));
  }


  @Override
  @Transactional
  public PassengerResponse patchPassenger(Long id, PassengerPatchRequest request) {
    log.info("Updating passenger with ID: {}", id);

    Passenger passenger = getExistsPassenger(id);

    applyPatch(passenger, request);

    log.info("Passenger with ID {} updated", id);

    return passengerMapper.toPassengerResponse(passenger);
  }

  @Override
  @Transactional
  public void deletePassenger(Long id) {
    log.info("Soft deleting passenger with ID: {}", id);

    Passenger passenger = getExistsPassenger(id);

    passenger.markAsDeleted();
    log.info("Passenger with ID {} soft deleted", id);
  }

  @Override
  public Passenger getExistsPassenger(Long id) {
    return passengerRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new PassengerNotFoundException(id));
  }

  private void validateEmailNotExists(String email) {
    if (passengerRepository.existsByEmailAndDeletedFalse(email)) {
      throw new DuplicateEmailException("Email already exists: " + email);
    }
  }

  private void validatePhoneNotExists(String phone) {
    if(passengerRepository.existsByPhoneAndDeletedFalse(phone)){
      throw new DuplicatePhoneException("Phone already exists: " + phone);
    }
  }

  private void applyPatch(Passenger passenger, PassengerPatchRequest request) {

    Optional.ofNullable(request.getName())
            .filter(name -> !name.isBlank())
            .ifPresent(passenger::changeName);

    Optional.ofNullable(request.getEmail())
            .filter(email -> !email.isBlank())
            .ifPresent(email -> {
              validateEmailNotExists(email);
              passenger.changeEmail(email);
            });

    Optional.ofNullable(request.getPhone())
            .filter(phone -> !phone.isBlank())
            .ifPresent(phone ->{
              validatePhoneNotExists(phone);
              passenger.changePhone(phone);
            });
  }
}