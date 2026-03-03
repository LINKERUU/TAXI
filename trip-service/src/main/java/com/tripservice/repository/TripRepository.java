package com.tripservice.repository;

import com.tripservice.model.Trip;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface TripRepository extends CrudRepository<Trip, Long> {
  Optional<Trip> findById(Long id);
  boolean existsById(Long id);

}
