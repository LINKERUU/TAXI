package com.driverservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DriverRequest {

  @NotBlank(message = "Имя обязательно")
  private String name;

  @Email(message = "Некорректный email")
  @NotBlank(message = "Email обязателен")
  private String email;

  @Pattern(regexp = "^\\+375(29|33|44|25)\\d{7}$",
          message = "Телефон должен быть в формате: +37529XXXXXXX")
  private String phone;

  @NotBlank(message = "Марка машины обязательна")
  private String carBrand;

  @NotBlank(message = "Цвет машины обязателен")
  private String carColor;

  @Pattern(regexp = "^[0-9]{4} [A-Z]{2}-[1-7]$",
          message = "Номер должен быть в формате: 1234 AB-1")
  private String carLicensePlate;
}