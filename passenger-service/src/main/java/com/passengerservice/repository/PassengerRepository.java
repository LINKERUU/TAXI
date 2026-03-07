package com.passengerservice.repository;

import com.passengerservice.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
  Optional<Passenger> findByIdAndDeletedFalse(Long id);

}
