package com.tripservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AddressRequest {
  @NotBlank(message = "City is required")
  @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
  private String city;

  @NotBlank(message = "Street is required")
  @Size(min = 2, max = 200, message = "Street must be between 2 and 200 characters")
  private String street;

  @NotBlank(message = "Building number is required")
  private String buildingNumber;
}
