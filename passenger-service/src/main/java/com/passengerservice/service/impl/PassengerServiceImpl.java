package com.passengerservice.service.impl;

import com.passengerservice.dto.PassengerRequest;
import com.passengerservice.dto.PassengerResponce;
import com.passengerservice.model.Passenger;
import com.passengerservice.repository.PassengerRepository;
import com.passengerservice.service.PassengerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {

  private final PassengerRepository passengerRepository;
  private final ModelMapper modelMapper = new ModelMapper();

  @Override
  @Transactional
  public PassengerResponce createPassenger(PassengerRequest request) {
    log.info("Creating passenger with email: {}", request.getEmail());

    // 2. Создание Passenger entity
    Passenger passenger = Passenger.builder()
            .name(request.getName())
            .email(request.getEmail())
            .phone(request.getPhone())
            .deleted(false)
            .build();

    // 3. Сохранение в БД
    Passenger savedPassenger = passengerRepository.save(passenger);
    log.info("Passenger created with ID: {}", savedPassenger.getId());

    return modelMapper.map(savedPassenger, PassengerResponce.class);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Passenger> getPassengerById(Long id) {
    log.info("Getting passenger with ID: {}", id);
    return passengerRepository.findByIdAndDeletedFalse(id);
  }

  @Override
  @Transactional
  public PassengerResponce updatePassenger(Long id, PassengerRequest request) throws Exception {
    log.info("Updating passenger with ID: {}", id);

    // 1. Находим существующего пассажира по ID из URL (а не из тела запроса!)
    Passenger existingPassenger = passengerRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new Exception("Passenger not found with id: " + id));

    // 3. Обновляем поля
    existingPassenger.setName(request.getName());
    existingPassenger.setEmail(request.getEmail());
    existingPassenger.setPhone(request.getPhone());

    // 4. Сохраняем обновленного пассажира
    Passenger updatedPassenger = passengerRepository.save(existingPassenger);
    log.info("Passenger with ID {} updated", id);

    // 5. Маппинг в DTO
    return modelMapper.map(updatedPassenger, PassengerResponce.class);
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