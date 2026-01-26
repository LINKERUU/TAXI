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

