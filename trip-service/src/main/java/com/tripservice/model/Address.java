package com.tripservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Address(
        @NotBlank(message = "City is required")
        @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
        String city,

        @NotBlank(message = "Street is required")
        @Size(min = 2, max = 200, message = "Street must be between 2 and 200 characters")
        String street,

        @NotBlank(message = "Building number is required")
        String buildingNumber
) { }
