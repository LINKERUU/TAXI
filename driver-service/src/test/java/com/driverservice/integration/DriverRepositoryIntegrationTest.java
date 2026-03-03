package com.driverservice.integration;

import com.driverservice.model.Car;
import com.driverservice.model.Driver;
import com.driverservice.repository.CarRepository;
import com.driverservice.repository.DriverRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DriverRepositoryIntegrationTest {

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
          .withDatabaseName("test")
          .withUsername("root")
          .withPassword("root");

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.datasource.username", postgres::getUsername);
  }

  @Autowired
  private DriverRepository driverRepository;

  @Autowired
  private CarRepository carRepository;

  private Driver driver;
  private Car car;

  @BeforeEach
  void setUp() {
    driverRepository.deleteAll();

    driverRepository.deleteAll();
    carRepository.deleteAll();

    car = Car.builder()
            .brand("Toyota")
            .color("Черный")
            .licensePlate("1234 AB-1")
            .build();
    car = carRepository.save(car);

    driver = Driver.builder()
            .name("Иван Петров")
            .email("ivan.petrov@example.com")
            .phone("+375291234567")
            .car(car)
            .deleted(false)
            .build();
  }

  @Test
  void saveAndFindById() {
    Driver saved = driverRepository.save(driver);
    Optional<Driver> found = driverRepository.findById(saved.getId());

    assertThat(found.isPresent()).isTrue();
    assertThat(found.get()).isEqualTo(saved);
    assertThat(found.get().getName()).isEqualTo(saved.getName());
    assertThat(found.get().getEmail()).isEqualTo(saved.getEmail());
    assertThat(found.get().getPhone()).isEqualTo(saved.getPhone());
    assertThat(found.get().getCar()).isEqualTo(saved.getCar());
  }

  @Test
  void existsByEmail_ReturnsTrue() {

    driverRepository.save(driver);

    boolean exists = driverRepository.existsByEmail("ivan.petrov@example.com");

    assertThat(exists).isTrue();
  }

  @Test
  void existsByEmail_ReturnsFalse() {

    boolean exists = driverRepository.existsByEmail("nonexistent@example.com");

    assertThat(exists).isFalse();
  }


  @Test
  void findByIdAndDeletedFalse_WhenNotDeleted_ReturnsDriver() {

    Driver saved = driverRepository.save(driver);

    Optional<Driver> found = driverRepository.findByIdAndDeletedFalse(saved.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getId()).isEqualTo(saved.getId());
    assertThat(found.get().isDeleted()).isFalse();
  }

  @Test
  void findByIdAndDeletedFalse_WhenDeleted_ReturnsEmpty() {

    driver.setDeleted(true);
    Driver saved = driverRepository.save(driver);

    Optional<Driver> found = driverRepository.findByIdAndDeletedFalse(saved.getId());

    assertThat(found).isEmpty();
  }

  @Test
  void updateDriver() {

    Driver saved = driverRepository.save(driver);

    saved.setName("Иван Петров Updated");
    saved.setPhone("+375299876543");
    Driver updated = driverRepository.save(saved);

    assertThat(updated.getName()).isEqualTo("Иван Петров Updated");
    assertThat(updated.getPhone()).isEqualTo("+375299876543");
    assertThat(updated.getEmail()).isEqualTo("ivan.petrov@example.com");
    assertThat(updated.getCar().getLicensePlate()).isEqualTo("1234 AB-1");
  }

  @Test
  void softDeleteDriver() {

    Driver saved = driverRepository.save(driver);

    saved.setDeleted(true);
    driverRepository.save(saved);

    Optional<Driver> found = driverRepository.findById(saved.getId());
    assertThat(found).isPresent();
    assertThat(found.get().isDeleted()).isTrue();

    Optional<Driver> active = driverRepository.findByIdAndDeletedFalse(saved.getId());
    assertThat(active).isEmpty();
  }

  @Test
  void restoreDriver() {

    driver.setDeleted(true);
    Driver saved = driverRepository.save(driver);

    saved.setDeleted(false);
    Driver restored = driverRepository.save(saved);

    assertThat(restored.isDeleted()).isFalse();

    Optional<Driver> found = driverRepository.findByIdAndDeletedFalse(restored.getId());
    assertThat(found).isPresent();
  }

  @Test
  void duplicateEmail_ThrowsException() {

    driverRepository.save(driver);

    Car car2 = Car.builder()
            .brand("BMW")
            .color("Синий")
            .licensePlate("5678 CD-2")
            .build();
    car2 = carRepository.save(car2);

    Driver duplicateDriver = Driver.builder()
            .name("Петр Иванов")
            .email("ivan.petrov@example.com")
            .phone("+375331234567")
            .car(car2)
            .deleted(false)
            .build();

    assertThrows(Exception.class, () -> {
      driverRepository.saveAndFlush(duplicateDriver);
    });
  }

  @Test
  void cascadeDeleteCar() {

    Driver saved = driverRepository.save(driver);
    Long carId = saved.getCar().getId();

    driverRepository.delete(saved);

    Optional<Driver> foundDriver = driverRepository.findById(saved.getId());
    Optional<Car> foundCar = carRepository.findById(carId);

    assertThat(foundDriver).isEmpty();
    assertThat(foundCar).isEmpty();
  }

  @Test
  void multipleDrivers_DifferentCars() {

    driverRepository.save(driver);

    Car car2 = Car.builder()
            .brand("BMW")
            .color("Синий")
            .licensePlate("5678 CD-2")
            .build();
    car2 = carRepository.save(car2);

    Driver driver2 = Driver.builder()
            .name("Анна Смирнова")
            .email("anna.smirnova@example.com")
            .phone("+375441234567")
            .car(car2)
            .deleted(false)
            .build();
    driverRepository.save(driver2);

    List<Driver> all = driverRepository.findAll();
    Optional<Driver> found1 = driverRepository.findById(driver.getId());
    Optional<Driver> found2 = driverRepository.findById(driver2.getId());

    // Then
    assertThat(all).hasSize(2);
    assertThat(found1).isPresent();
    assertThat(found2).isPresent();
    assertThat(found1.get().getCar().getLicensePlate()).isEqualTo("1234 AB-1");
    assertThat(found2.get().getCar().getLicensePlate()).isEqualTo("5678 CD-2");
  }
}

