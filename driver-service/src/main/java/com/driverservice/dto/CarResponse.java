package com.driverservice.dto;

import lombok.Data;

@Data
public class CarResponse {
  private Long id;
  private String brand;
  private String color;
  private String licensePlate;
}
