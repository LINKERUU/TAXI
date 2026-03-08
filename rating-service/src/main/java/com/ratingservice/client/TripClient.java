package com.ratingservice.client;

import com.ratingservice.client.dto.TripResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "trip-service")
public interface TripClient {

  @GetMapping("/api/trips/{tripId}")
  TripResponse getTripById(@PathVariable("tripId") Long tripId);

}