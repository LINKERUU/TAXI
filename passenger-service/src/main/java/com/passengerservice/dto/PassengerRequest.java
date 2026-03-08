package com.passengerservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class PassengerRequest {

  @NotBlank(message = "Name must not be empty")
  @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
  private String name;

  @NotBlank(message = "Email must not be empty")
  @Email(message = "Email must be valid")
  @Size(max = 255, message = "Email must not exceed 255 characters")
  private String email;

  @NotBlank(message = "phone must not be empty")
  @Pattern(
          regexp = "^\\+375\\d{9}$",
          message = "Phone must be in format: +375XXXXXXXXX"
  )
  private String phone;
}