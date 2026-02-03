package com.tripservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TripRequest {

  @NotNull(message = "Driver ID is required")
  private Long driverId;

  @NotNull(message = "Passenger ID is required")
  private Long passengerId;

  @NotBlank(message = "Pickup address is required")
  @Size(max = 255)
  private String pickupAddress;

  @NotBlank(message = "Destination address is required")
  @Size(max = 255)
  private String destinationAddress;

  @NotNull(message = "Price is required")
  @DecimalMin(value = "0.0", inclusive = false)
  @Digits(integer = 10, fraction = 2)
  private BigDecimal price;
}