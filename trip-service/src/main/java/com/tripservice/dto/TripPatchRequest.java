package com.tripservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
  private AddressRequest pickupAddress;

  @Valid
  private AddressRequest destinationAddress;

  @DecimalMin(value = "0.0", inclusive = false)
  @Digits(integer = 10, fraction = 2)
  private BigDecimal price;
}

