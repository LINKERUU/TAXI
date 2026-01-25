package com.driverservice.dto;

import lombok.Data;

@Data
public class DriverResponse {
  private Long id;
  private String name;
  private String email;
  private String phone;
  private CarResponse car;
}

@Data
class CarResponse {
  private Long id;
  private String brand;
  private String color;
  private String licensePlate;
}