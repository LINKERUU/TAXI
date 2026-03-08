package com.ratingservice.exception;

import com.ratingservice.exception.custom.*;
import com.ratingservice.exception.dto.ErrorCode;
import com.ratingservice.exception.dto.ErrorResponse;
import com.ratingservice.exception.dto.ValidationErrorResponse;
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

  @ExceptionHandler(RatingNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handlePassengerNotFound(
          RatingNotFoundException ex,
          HttpServletRequest request) {

    log.warn("Rating not found: {}", ex.getMessage());

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

  @ExceptionHandler(TripNotCompletedException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleTripNotCompleted(
          BaseException e,
          HttpServletRequest request
  ) {

    log.warn("Trip status not completed error: {}", e.getMessage());

    return new ErrorResponse(
            e.getErrorCode(),
            e.getMessage(),
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI(),
            LocalDateTime.now());
  }

  @ExceptionHandler(RatingAlreadyExistsException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorResponse handleRatingExists(
          RatingAlreadyExistsException e,
          HttpServletRequest request
  ) {

    log.warn("Rating already exists error: {}", e.getMessage());

    return new ErrorResponse(
            e.getErrorCode(),
            e.getMessage(),
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI(),
            LocalDateTime.now());
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