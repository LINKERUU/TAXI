package com.driverservice.repository;

import com.driverservice.model.Driver;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
  boolean existsByEmail(@Email(message = "Некорректный email") @NotBlank(message = "Email обязателен") String email);

  Optional<Driver> findByIdAndDeletedFalse(Long id);
}
