package com.driverservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CarResponse {
  private Long id;
  private String brand;
  private String color;
  private String licensePlate;
}
