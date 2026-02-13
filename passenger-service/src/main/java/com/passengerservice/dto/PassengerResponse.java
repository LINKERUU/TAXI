package com.passengerservice.dto;

import lombok.Builder;
import lombok.Data;

import java.lang.management.LockInfo;

@Data
@Builder
public class PassengerResponse {
  private Long id;
  private String name;
  private String email;
  private String phone;
}
