package com.driverservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DriverRequest {

  @NotBlank(message = "Name must not be empty")
  @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
  private String name;

  @NotBlank(message = "Email must not be empty")
  @Email(message = "Email must be valid")
  private String email;

  @NotBlank(message = "Phone must not be empty")
  @Pattern(
          regexp = "^\\+375\\d{9}$",
          message = "Phone must be in format: +375XXXXXXXXX"
  )
  private String phone;

  @NotBlank(message = "Car brand must not be empty")
  @Size(min = 2, max = 50, message = "Name must be between 2 and 100 characters")
  private String carBrand;

  @NotBlank(message = "Car color must not be empty")
  @Size(min = 2, max = 30, message = "Name must be between 2 and 100 characters")
  private String carColor;

  @NotBlank(message = "Car license plate must not be empty")
  @Pattern(regexp = "^[0-9]{4} [A-Z]{2}-[1-7]$",
          message = "License Plate must be in format: 1234 AB-1")
  private String carLicensePlate;
}