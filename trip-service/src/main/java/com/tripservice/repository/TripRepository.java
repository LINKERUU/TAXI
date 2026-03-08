package com.tripservice.repository;

import com.tripservice.model.Trip;
import org.springframework.data.repository.CrudRepository;

public interface TripRepository extends CrudRepository<Trip, Long> {
}
