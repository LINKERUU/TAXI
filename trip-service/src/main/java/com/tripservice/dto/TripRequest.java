package com.tripservice.dto;

import com.tripservice.model.Address;
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
public class TripRequest {

  @NotNull(message = "Driver ID is required")
  @Positive(message = "Driver ID must be positive")
  private Long driverId;

  @NotNull(message = "Passenger ID is required")
  @Positive(message = "Passenger ID must be positive")
  private Long passengerId;

  @NotNull(message = "Pickup address is required")
  @Valid
  private Address pickupAddress;

  @NotNull(message = "Destination address is required")
  @Valid
  private Address destinationAddress;

  @NotNull(message = "Price is required")
  @DecimalMin(value = "0.0", inclusive = false)
  @Digits(integer = 10, fraction = 2)
  private BigDecimal price;
}

