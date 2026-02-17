package com.tripservice.repository;

import com.tripservice.model.Trip;
import org.springframework.data.repository.CrudRepository;

<<<<<<< Updated upstream
public interface TripRepository extends CrudRepository<Trip, Integer> {
  void deleteById(Long id);
  Trip findById(Long id);
=======
import java.util.Optional;

public interface TripRepository extends CrudRepository<Trip, Long> {
  Optional<Trip> findById(Long id);
>>>>>>> Stashed changes
  boolean existsById(Long id);

}
