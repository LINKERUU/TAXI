package com.driverservice.dto;

public record DriverResponse (
   Long id,
   String name,
   String email,
   String phone,
   CarResponse car
){}

