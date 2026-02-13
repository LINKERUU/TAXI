package com.tripservice.dto;


import com.tripservice.model.enums.TripStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class StatusUpdateRequest{
        @NotNull(message = "Status is required")
        TripStatus status;
}