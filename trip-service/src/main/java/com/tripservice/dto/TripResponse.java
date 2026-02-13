package com.tripservice.dto;

import com.tripservice.model.enums.TripStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TripResponse {
  private Long id;
  private Long driverId;
  private Long passengerId;
  private String pickupAddress;
  private String destinationAddress;
  private TripStatus status;
  private LocalDateTime orderTime;
  private BigDecimal price;
}