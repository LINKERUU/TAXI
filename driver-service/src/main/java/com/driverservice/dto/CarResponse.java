package com.driverservice.dto;

public record CarResponse(
        Long id,
        String brand,
        String color,
        String licensePlate
) {
}
