package com.driverservice.repository;

import com.driverservice.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
  Optional<Driver> findByIdAndDeletedFalse(Long id);

  boolean existsByEmailAndDeletedFalse(String email);

  boolean existsByPhoneAndDeletedFalse(String phone);
}
