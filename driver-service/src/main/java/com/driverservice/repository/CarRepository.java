package com.driverservice.repository;

import com.driverservice.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
  boolean existsByLicensePlate(String plate);
}
