package com.passengerservice.exception;

import com.passengerservice.exception.custom.BaseException;
import com.passengerservice.exception.custom.DuplicateEmailException;
import com.passengerservice.exception.custom.DuplicatePhoneException;
import com.passengerservice.exception.custom.PassengerNotFoundException;
import com.passengerservice.exception.dto.ErrorCode;
import com.passengerservice.exception.dto.ErrorResponse;
import com.passengerservice.exception.dto.ValidationErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(PassengerNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handlePassengerNotFound(
          PassengerNotFoundException ex,
          HttpServletRequest request) {

    log.warn("Passenger not found: {}", ex.getMessage());

    return new ErrorResponse(
            ex.getErrorCode(),
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value(),
            request.getRequestURI(),
            LocalDateTime.now()
    );
  }

  @ExceptionHandler({DuplicateEmailException.class, DuplicatePhoneException.class})
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorResponse handleDuplicateEmail(
          BaseException ex,
          HttpServletRequest request) {

    log.warn("Duplicate error : {}", ex.getMessage());

    return new ErrorResponse(
            ex.getErrorCode(),
            ex.getMessage(),
            HttpStatus.CONFLICT.value(),
            request.getRequestURI(),
            LocalDateTime.now()
    );
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ValidationErrorResponse handleValidationExceptions(
          MethodArgumentNotValidException ex,
          HttpServletRequest request) {

    log.warn("Validation error: {}", ex.getMessage());

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    return new ValidationErrorResponse(
            ErrorCode.VALIDATION_ERROR,
            "Validation failed",
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI(),
            LocalDateTime.now(),
            errors
    );
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleUnexpectedException(
          Exception ex,
          HttpServletRequest request) {

    log.error("Unexpected error occurred", ex);

    return new ErrorResponse(
            ErrorCode.INTERNAL_ERROR,
            "An unexpected error occurred",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            request.getRequestURI(),
            LocalDateTime.now()
    );
  }
}