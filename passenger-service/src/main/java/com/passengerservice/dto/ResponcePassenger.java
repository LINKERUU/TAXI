package com.passengerservice.dto;

import lombok.Data;

import java.lang.management.LockInfo;

@Data
public class ResponcePassenger {
  private Long id;
  private String name;
  private String email;
  private String phone;
}
