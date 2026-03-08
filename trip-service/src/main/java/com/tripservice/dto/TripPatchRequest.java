package com.tripservice.dto;

import com.tripservice.model.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class TripPatchRequest {

  @Positive(message = "Driver ID must be positive")
  private Long driverId;

  @Valid
  private Address pickupAddress;

  @Valid
  private Address destinationAddress;

  @DecimalMin(value = "0.0", inclusive = false)
  @Digits(integer = 10, fraction = 2)
  private BigDecimal price;
}

