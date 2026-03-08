package com.passengerservice.dto;


public record PassengerResponse (
  Long id,
  String name,
  String email,
  String phone
){}
