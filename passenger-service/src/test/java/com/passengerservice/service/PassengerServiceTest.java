package com.passengerservice.service;

import com.passengerservice.dto.PassengerPatchRequest;
import com.passengerservice.dto.PassengerRequest;
import com.passengerservice.dto.PassengerResponse;
import com.passengerservice.exception.custom.DuplicateEmailException;
import com.passengerservice.exception.custom.DuplicatePhoneException;
import com.passengerservice.exception.custom.PassengerNotFoundException;
import com.passengerservice.mapper.PassengerMapper;
import com.passengerservice.model.Passenger;
import com.passengerservice.repository.PassengerRepository;
import com.passengerservice.service.impl.PassengerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PassengerServiceTest {

  @Mock
  private PassengerRepository passengerRepository;

  @Mock
  private PassengerMapper passengerMapper;

  @InjectMocks
  private PassengerServiceImpl passengerService;

  private Passenger passenger;
  private PassengerRequest passengerRequest;
  private PassengerResponse passengerResponse;

  private static final Long PASSENGER_ID = 1L;
  private static final Long NON_EXISTENT_ID = 999L;
  private static final String NAME = "John Doe";
  private static final String EMAIL = "john.doe@gmail.com";
  private static final String PHONE = "+375447743555";
  private static final String UPDATED_NAME = "John Updated";
  private static final String UPDATED_PHONE = "+375299876543";
  private static final String UPDATED_EMAIL = "genri.long@gmail.com";

  @BeforeEach
  public void setUp() {

    passenger = new Passenger(NAME,EMAIL,PHONE);

    passengerRequest = PassengerRequest.builder()
            .name(NAME)
            .email(EMAIL)
            .phone(PHONE)
            .build();

    passengerResponse = new PassengerResponse(PASSENGER_ID, NAME, EMAIL, PHONE);
  }

  @Test
  @DisplayName("Should create passenger successfully")
  public void  createPassengerSuccess() {
    when(passengerRepository.existsByEmailAndDeletedFalse(EMAIL)).thenReturn(false);
    when(passengerMapper.toEntity(passengerRequest)).thenReturn(passenger);
    when(passengerRepository.save(passenger)).thenReturn(passenger);
    when(passengerMapper.toPassengerResponse(passenger)).thenReturn(passengerResponse);

    PassengerResponse result = passengerService.createPassenger(passengerRequest);

    assertNotNull(result);
    assertEquals(PASSENGER_ID, result.id());
    assertEquals(NAME, result.name());
    assertEquals(EMAIL, result.email());
    assertEquals(PHONE, result.phone());

    verify(passengerRepository).existsByEmailAndDeletedFalse(EMAIL);
    verify(passengerMapper).toEntity(passengerRequest);
    verify(passengerRepository).save(passenger);
    verify(passengerMapper).toPassengerResponse(passenger);
  }

  @Test
  @DisplayName("Should throw DuplicatePhoneException when creating passenger")
  public void createPassengerThrowsExceptionWhenPhoneExists() {
    when(passengerRepository.existsByEmailAndDeletedFalse(EMAIL)).thenReturn(false);
    when(passengerRepository.existsByPhoneAndDeletedFalse(PHONE)).thenReturn(true);
    assertThrows(DuplicatePhoneException.class,
            () -> passengerService.createPassenger(passengerRequest));

    verify(passengerRepository).existsByEmailAndDeletedFalse(EMAIL);
    verify(passengerRepository).existsByPhoneAndDeletedFalse(PHONE);
    verify(passengerMapper, never()).toEntity(any());
    verify(passengerRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should throw DuplicateEmailException when creating passenger")
  public void createPassengerThrowsExceptionWhenEmailExists() {
    when(passengerRepository.existsByEmailAndDeletedFalse(EMAIL)).thenReturn(true);

    assertThrows(DuplicateEmailException.class,
            () -> passengerService.createPassenger(passengerRequest));

    verify(passengerRepository).existsByEmailAndDeletedFalse(EMAIL);
    verify(passengerMapper, never()).toEntity(any());
    verify(passengerRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should return passenger when ID exists")
  public void getPassengerByIdSuccess() {
    when(passengerRepository.findByIdAndDeletedFalse(PASSENGER_ID))
            .thenReturn(Optional.of(passenger));
    when(passengerMapper.toPassengerResponse(passenger)).thenReturn(passengerResponse);

    PassengerResponse result = passengerService.getPassengerById(PASSENGER_ID);

    assertNotNull(result);
    assertEquals(PASSENGER_ID, result.id());

    verify(passengerRepository).findByIdAndDeletedFalse(PASSENGER_ID);
    verify(passengerMapper).toPassengerResponse(passenger);
  }

  @Test
  @DisplayName("Should throw PassengerNotFoundException when ID does not exist")
  public void getPassengerByIdThrowsExceptionWhenNotFound() {
    when(passengerRepository.findByIdAndDeletedFalse(NON_EXISTENT_ID))
            .thenReturn(Optional.empty());

    PassengerNotFoundException exception = assertThrows(PassengerNotFoundException.class,
            () -> passengerService.getPassengerById(NON_EXISTENT_ID));

    assertEquals("Passenger not found with id " + NON_EXISTENT_ID, exception.getMessage());
    verify(passengerRepository).findByIdAndDeletedFalse(NON_EXISTENT_ID);
    verify(passengerMapper, never()).toPassengerResponse(any());
  }

  @Test
  @DisplayName("Should update passenger fields when valid patch request provided")
  public void patchPassengerSuccess() {
    PassengerPatchRequest patchRequest = PassengerPatchRequest.builder()
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .build();

    when(passengerRepository.findByIdAndDeletedFalse(PASSENGER_ID))
            .thenReturn(Optional.of(passenger));

    when(passengerMapper.toPassengerResponse(any(Passenger.class)))
            .thenReturn(new PassengerResponse(PASSENGER_ID, UPDATED_NAME, UPDATED_EMAIL, UPDATED_PHONE));

    PassengerResponse result = passengerService.patchPassenger(PASSENGER_ID, patchRequest);

    assertNotNull(result);
    assertEquals(PASSENGER_ID, result.id());
    assertEquals(UPDATED_NAME, result.name());
    assertEquals(UPDATED_EMAIL, result.email());
    assertEquals(UPDATED_PHONE, result.phone());

    verify(passengerRepository).findByIdAndDeletedFalse(PASSENGER_ID);
    verify(passengerMapper).toPassengerResponse(passenger);
  }

  @Test
  @DisplayName("Should keep original fields when patch request has empty fields")
  public void patchPassengerEmptyFields() {
    PassengerPatchRequest patchRequest = PassengerPatchRequest.builder()
            .build();

    when(passengerRepository.findByIdAndDeletedFalse(PASSENGER_ID))
            .thenReturn(Optional.of(passenger));

    when(passengerMapper.toPassengerResponse(any(Passenger.class)))
            .thenReturn(new PassengerResponse(PASSENGER_ID, NAME, EMAIL, PHONE));

    PassengerResponse result = passengerService.patchPassenger(PASSENGER_ID, patchRequest);

    assertNotNull(result);
    assertEquals(PASSENGER_ID, result.id());
    assertEquals(NAME, result.name());
    assertEquals(EMAIL, result.email());
    assertEquals(PHONE, result.phone());

    verify(passengerRepository).findByIdAndDeletedFalse(PASSENGER_ID);
    verify(passengerMapper).toPassengerResponse(passenger);
  }

  @Test
  @DisplayName("Should soft delete passenger when ID exists")
  public void deletePassengerSuccess() {
    when(passengerRepository.findByIdAndDeletedFalse(PASSENGER_ID))
            .thenReturn(Optional.of(passenger));

    passengerService.deletePassenger(PASSENGER_ID);

    assertTrue(passenger.isDeleted());
    verify(passengerRepository).findByIdAndDeletedFalse(PASSENGER_ID);
  }

  @Test
  @DisplayName("Should throw PassengerNotFoundException when deleting non-existent passenger")
  public void deletePassengerThrowsExceptionWhenPassengerNotFound() {
    when(passengerRepository.findByIdAndDeletedFalse(NON_EXISTENT_ID))
            .thenReturn(Optional.empty());

    PassengerNotFoundException exception = assertThrows(PassengerNotFoundException.class,
            () -> passengerService.deletePassenger(NON_EXISTENT_ID));

    assertEquals("Passenger not found with id " + NON_EXISTENT_ID, exception.getMessage());
    verify(passengerRepository).findByIdAndDeletedFalse(NON_EXISTENT_ID);
  }
}