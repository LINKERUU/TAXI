package com.passengerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.passengerservice.dto.PassengerPatchRequest;
import com.passengerservice.dto.PassengerRequest;
import com.passengerservice.dto.PassengerResponse;
import com.passengerservice.exception.custom.DuplicateEmailException;
import com.passengerservice.exception.custom.PassengerNotFoundException;
import com.passengerservice.service.PassengerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PassengerController.class)
public class PassengerControllerTest {

  private static final String BASE_URL = "/api/passengers";
  private static final String BASE_URL_WITH_ID = BASE_URL + "/{id}";
  private static final Long ID = 1L;

  private static final String NAME = "John Doe";
  private static final String EMAIL = "john.doe@example.com";
  private static final String PHONE = "+375291234567";

  private static final String UPDATED_NAME = "John Updated";
  private static final String UPDATED_EMAIL = "john.doe1@example.com";
  private static final String INVALID_EMAIL = "invalid-email";
  private static final String EMPTY = "";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private PassengerService passengerService;


  private String toJson(Object object) {
    return assertDoesNotThrow(() -> objectMapper.writeValueAsString(object));
  }

  @Test
  @DisplayName("POST should return 201 when valid")
  public void shouldCreatePassenger() {

    PassengerRequest request = new PassengerRequest(NAME, EMAIL, PHONE);
    PassengerResponse response = new PassengerResponse(ID, NAME, EMAIL, PHONE);

    when(passengerService.createPassenger(any())).thenReturn(response);

    assertDoesNotThrow(() ->
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(ID))
                    .andExpect(jsonPath("$.name").value(NAME))
    );

    verify(passengerService).createPassenger(any());
  }

  @Test
  @DisplayName("POST should return 400 when invalid")
  public void shouldReturnBadRequest() {

    PassengerRequest invalid =
            new PassengerRequest(EMPTY, INVALID_EMAIL, EMPTY);

    assertDoesNotThrow(() ->
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(invalid)))
                    .andExpect(status().isBadRequest())
    );

    verify(passengerService, never()).createPassenger(any());
  }

  @Test
  @DisplayName("POST should return 409 when email duplicated")
  public void shouldReturnConflict() {

    PassengerRequest request =
            new PassengerRequest(NAME, EMAIL, PHONE);

    when(passengerService.createPassenger(any()))
            .thenThrow(new DuplicateEmailException("Duplicate email"));

    assertDoesNotThrow(() ->
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isConflict())
    );

    verify(passengerService).createPassenger(any());
  }

  @Test
  @DisplayName("GET should return 200 when found")
  public void shouldReturnPassenger() {

    PassengerResponse response =
            new PassengerResponse(ID, NAME, EMAIL, PHONE);

    when(passengerService.getPassengerById(ID)).thenReturn(response);

    assertDoesNotThrow(() ->
            mockMvc.perform(get(BASE_URL_WITH_ID, ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(ID))
                    .andExpect(jsonPath("$.name").value(NAME))
                    .andExpect(jsonPath("$.email").value(EMAIL))
                    .andExpect(jsonPath("$.phone").value(PHONE))
    );

    verify(passengerService).getPassengerById(ID);
  }

  @Test
  @DisplayName("GET should return 404 when missing")
  public void shouldReturnNotFound() {

    when(passengerService.getPassengerById(ID))
            .thenThrow(new PassengerNotFoundException(ID));

    assertDoesNotThrow(() ->
            mockMvc.perform(get(BASE_URL_WITH_ID, ID))
                    .andExpect(status().isNotFound())
    );

    verify(passengerService).getPassengerById(ID);
  }

  @Test
  @DisplayName("PATCH should return 200 when updated")
  public void shouldPatchPassenger() {

    PassengerPatchRequest request = PassengerPatchRequest.builder()
            .email(UPDATED_EMAIL)
            .build();

    PassengerResponse response =
            new PassengerResponse(ID, NAME, UPDATED_EMAIL, PHONE);

    when(passengerService.patchPassenger(eq(ID), any()))
            .thenReturn(response);

    assertDoesNotThrow(() ->
            mockMvc.perform(patch(BASE_URL_WITH_ID, ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value(UPDATED_EMAIL))
    );

    verify(passengerService).patchPassenger(eq(ID), any());
  }

  @Test
  @DisplayName("PATCH should return 404 when missing")
  public void shouldReturnNotFoundWhenPatching() {

    PassengerPatchRequest request = PassengerPatchRequest.builder()
            .name(UPDATED_NAME)
            .build();

    when(passengerService.patchPassenger(eq(ID), any()))
            .thenThrow(new PassengerNotFoundException(ID));

    assertDoesNotThrow(() ->
            mockMvc.perform(patch(BASE_URL_WITH_ID, ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                    .andExpect(status().isNotFound())
    );
  }

  @Test
  @DisplayName("DELETE should return 204")
  public void shouldDeletePassenger() {

    assertDoesNotThrow(() ->
            mockMvc.perform(delete(BASE_URL_WITH_ID, ID))
                    .andExpect(status().isNoContent())
    );

    verify(passengerService).deletePassenger(ID);
  }

  @Test
  @DisplayName("DELETE should return 404 when missing")
  public void shouldReturnNotFoundWhenDeleting() {

    doThrow(new PassengerNotFoundException(ID))
            .when(passengerService).deletePassenger(ID);

    assertDoesNotThrow(() ->
            mockMvc.perform(delete(BASE_URL_WITH_ID, ID))
                    .andExpect(status().isNotFound())
    );
  }
}