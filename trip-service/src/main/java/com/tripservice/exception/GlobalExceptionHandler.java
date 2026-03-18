package com.tripservice.exception;

import com.tripservice.exception.custom.*;
import com.tripservice.exception.dto.ErrorCode;
import com.tripservice.exception.dto.ErrorResponse;
import com.tripservice.exception.dto.ValidationErrorResponse;
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

  @ExceptionHandler(TripNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleTripNotFound(
          TripNotFoundException ex,
          HttpServletRequest request) {

    log.warn("Trip not found: {}", ex.getMessage());

    return new ErrorResponse(
            ex.getErrorCode(),
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value(),
            request.getRequestURI(),
            LocalDateTime.now()
    );
  }

  @ExceptionHandler(ServiceUnavailableException.class)
  @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
  public ErrorResponse handleServiceUnavailable(
          BaseException ex,
          HttpServletRequest request
  ) {
    log.warn("Service unavailable: {}", ex.getMessage());
    return new ErrorResponse(
            ex.getErrorCode(),
            ex.getMessage(),
            HttpStatus.SERVICE_UNAVAILABLE.value(),
            request.getRequestURI(),
            LocalDateTime.now()
    );
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleTransactionStatusException(
          InvalidTransitionStatusException ex,
          HttpServletRequest request
  ) {
    log.warn("Transaction status error: {}", ex.getMessage());
    return new ErrorResponse(
            ex.getErrorCode(),
            ex.getMessage(),
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI(),
            LocalDateTime.now()
    );
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ValidationErrorResponse handleValidationException(
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