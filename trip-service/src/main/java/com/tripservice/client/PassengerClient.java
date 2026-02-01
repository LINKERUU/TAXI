package com.tripservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "passenger-service")
public interface  PassengerClient {

  @GetMapping("/api/passengers/{passengerId}")
  PassengerResponse getPassengerById(@PathVariable("passengerId") Long passengerId);

  record PassengerResponse(Long id, String name, String email, String phone) {}
}