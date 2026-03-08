package com.driverservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DriverPatchRequest {

  @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
  private String name;

  @Email(message = "Email must be valid")
  private String email;

  @Pattern(
          regexp = "^\\+375\\d{9}$",
          message = "Phone must be in format: +375XXXXXXXXX"
  )
  private String phone;

  @Size(min = 2, max = 50, message = "Car brand must be between 2 and 100 characters")
  private String carBrand;

  @Size(min = 2, max = 30, message = "Car color must be between 2 and 100 characters")
  private String carColor;

  @Pattern(regexp = "^[0-9]{4} [A-Z]{2}-[1-7]$",
          message = "License Plate must be in format: 1234 AB-1")
  private String carLicensePlate;
}
