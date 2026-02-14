package com.passengerservice.service;

import com.passengerservice.dto.PassengerRequest;
import com.passengerservice.dto.PassengerResponse;
import com.passengerservice.mapper.PassengerMapper;
import com.passengerservice.model.Passenger;
import com.passengerservice.repository.PassengerRepository;
import com.passengerservice.service.impl.PassengerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PassengerServiceTests {

  @Mock
  private PassengerRepository passengerRepository;

  @Mock
  private PassengerMapper passengerMapper;

  @InjectMocks
  private PassengerServiceImpl passengerService;

  private Passenger passenger;
  private PassengerRequest passengerRequest;
  private PassengerResponse passengerResponse;

  @BeforeEach
  void setUp() {
    passenger = Passenger.builder()
            .id(1L)
            .name("John Doe")
            .email("john.doe@gmail.com")
            .phone("+375447743555")
            .deleted(false)
            .build();

    passengerRequest = PassengerRequest.builder()
            .name("John Doe")
            .email("john.doe@gmail.com")
            .phone("+375447743555")
            .build();

    passengerResponse = PassengerResponse.builder()
            .id(1L)
            .name("John Doe")
            .email("john.doe@gmail.com")
            .phone("+375447743555")
            .build();
  }

  @Test
  void  createPassenger_Success() {

    when(passengerMapper.toEntity(any(PassengerRequest.class))).thenReturn(passenger);
    when(passengerRepository.save(any(Passenger.class))).thenReturn(passenger);
    when(passengerMapper.toPassengerResponse(any(Passenger.class))).thenReturn(passengerResponse);

    PassengerResponse passengerResponse = passengerService.createPassenger(passengerRequest);

    assertNotNull(passengerResponse);
    assertEquals(passengerResponse.getId(), passenger.getId());
    assertEquals(passengerResponse.getName(), passenger.getName());
    assertEquals(passengerResponse.getEmail(), passenger.getEmail());
    assertEquals(passengerResponse.getPhone(), passenger.getPhone());

    verify(passengerMapper, times(1)).toEntity(passengerRequest);
    verify(passengerRepository, times(1)).save(passenger);
    verify(passengerMapper, times(1)).toPassengerResponse(passenger);
  }

  @Test
  void createPassenger_Failure() {
    when(passengerMapper.toEntity(any(PassengerRequest.class))).thenReturn(passenger);
    when(passengerRepository.save(any(Passenger.class))).thenThrow(RuntimeException.class);
    assertThrows(RuntimeException.class, () -> passengerService.createPassenger(passengerRequest));
    verify(passengerRepository, times(1)).save(passenger);
    verify(passengerMapper, times(1)).toEntity(passengerRequest);
  }

  @Test
  void getPassengerById_PassengerExists_ReturnsPassenger() {

    Long passengerId = 1L;
    when(passengerRepository.findByIdAndDeletedFalse(passengerId))
            .thenReturn(Optional.of(passenger));
    when(passengerMapper.toPassengerResponse(passenger)).thenReturn(passengerResponse);

    PassengerResponse result = passengerService.getPassengerById(passengerId);

    assertNotNull(result);
    assertEquals(passengerId, result.getId());

    verify(passengerRepository, times(1)).findByIdAndDeletedFalse(passengerId);
    verify(passengerMapper, times(1)).toPassengerResponse(passenger);
  }

  @Test
  void getPassengerById_PassengerNotFound_ThrowsException() {

    Long passengerId = 999L;
    when(passengerRepository.findByIdAndDeletedFalse(passengerId))
            .thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(RuntimeException.class,
            () -> passengerService.getPassengerById(passengerId));

    assertEquals("Passenger not found" + passengerId, exception.getMessage());
    verify(passengerRepository, times(1)).findByIdAndDeletedFalse(passengerId);
    verify(passengerMapper, never()).toPassengerResponse(any());
  }

  @Test
  void updatePassenger_Success() throws Exception {

    Long passengerId = 1L;
    PassengerRequest updateRequest = PassengerRequest.builder()
            .name("John Updated")
            .phone("+375299876543")
            .build();

    Passenger updatedPassenger = Passenger.builder()
            .id(passengerId)
            .name("John Updated")
            .email("john.doe@example.com")
            .phone("+375299876543")
            .build();

    PassengerResponse updatedResponse = PassengerResponse.builder()
            .id(passengerId)
            .name("John Updated")
            .email("john.doe@example.com")
            .phone("+375299876543")
            .build();

    when(passengerRepository.findByIdAndDeletedFalse(passengerId))
            .thenReturn(Optional.of(passenger));
    when(passengerRepository.save(any(Passenger.class))).thenReturn(updatedPassenger);
    when(passengerMapper.toPassengerResponse(updatedPassenger)).thenReturn(updatedResponse);

    PassengerResponse result = passengerService.updatePassenger(passengerId, updateRequest);

    assertNotNull(result);
    assertEquals(passengerId, result.getId());
    assertEquals("John Updated", result.getName());
    assertEquals("+375299876543", result.getPhone());

    verify(passengerRepository, times(1)).findByIdAndDeletedFalse(passengerId);
    verify(passengerMapper, times(1)).updatePassengerFromRequest(updateRequest, passenger);
    verify(passengerRepository, times(1)).save(passenger);
    verify(passengerMapper, times(1)).toPassengerResponse(updatedPassenger);
  }

  @Test
  void updatePassenger_PassengerNotFound_ThrowsException() {
    Long passengerId = 999L;
    when(passengerRepository.findByIdAndDeletedFalse(passengerId))
            .thenReturn(Optional.empty());

    Exception exception = assertThrows(Exception.class,
            () -> passengerService.updatePassenger(passengerId, passengerRequest));

    assertEquals("Passenger not found with id: " + passengerId, exception.getMessage());
    verify(passengerRepository, times(1)).findByIdAndDeletedFalse(passengerId);
    verify(passengerMapper, never()).updatePassengerFromRequest(any(), any());
    verify(passengerRepository, never()).save(any());
  }

  @Test
  void deletePassenger_Success() throws Exception {

    Long passengerId = 1L;
    Passenger passengerToDelete = Passenger.builder()
            .id(passengerId)
            .name("John Doe")
            .email("john.doe@example.com")
            .deleted(false)
            .build();

    when(passengerRepository.findByIdAndDeletedFalse(passengerId))
            .thenReturn(Optional.of(passengerToDelete));
    when(passengerRepository.save(any(Passenger.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    passengerService.deletePassenger(passengerId);

    assertTrue(passengerToDelete.isDeleted());
    verify(passengerRepository, times(1)).findByIdAndDeletedFalse(passengerId);
    verify(passengerRepository, times(1)).save(passengerToDelete);
  }

  @Test
  void deletePassenger_PassengerNotFound_ThrowsException() {

    Long passengerId = 999L;
    when(passengerRepository.findByIdAndDeletedFalse(passengerId))
            .thenReturn(Optional.empty());

    Exception exception = assertThrows(Exception.class,
            () -> passengerService.deletePassenger(passengerId));

    assertEquals("Passenger not found with id: " + passengerId, exception.getMessage());
    verify(passengerRepository, times(1)).findByIdAndDeletedFalse(passengerId);
    verify(passengerRepository, never()).save(any());
  }


}