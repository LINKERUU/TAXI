package com.tripservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "passenger-service",
        configuration = FeignClient.class,
        fallback = PassengerClient.PassengerResponseFallback.class
)
public interface  PassengerClient {

  @GetMapping("/api/passengers/{passengerId}")
  @CircuitBreaker(name = "passengerService", fallbackMethod = "getPassengerByIdFallback")
  PassengerResponse getPassengerById(@PathVariable("passengerId") Long passengerId);

  record PassengerResponse(Long id, String name, String email, String phone) {}

  @Component
  class PassengerResponseFallback implements PassengerClient {

    @Override
    public PassengerResponse getPassengerById(Long passengerId){
      return new PassengerResponse(
              passengerId,
              "Unknown Passenger",
              "unknown@email.com",
              "+00000000000"
      );
    }
  }
}