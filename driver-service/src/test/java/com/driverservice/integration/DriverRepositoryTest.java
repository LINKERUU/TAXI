package com.driverservice.integration;

import com.driverservice.model.Car;
import com.driverservice.model.Driver;
import com.driverservice.repository.CarRepository;
import com.driverservice.repository.DriverRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DriverRepositoryTest extends TestConfig {

  private static final String NAME = "Иван Петров";
  private static final String EMAIL = "ivan.petrov@example.com";
  private static final String UPDATED_EMAIL = "john.doeh@example.com";
  private static final String PHONE = "+375391234567";
  private static final String UPDATED_NAME = "Иван Петров Обновленный";

  private static final String CAR_BRAND = "Toyota";
  private static final String CAR_COLOR = "Черный";
  private static final String CAR_LICENSE_PLATE = "9999 AB-9";

  @Autowired
  private DriverRepository driverRepository;

  @Autowired
  private CarRepository carRepository;

  private Driver driver;
  private Car car;

  @BeforeEach
  public void setUp() {
    car = new Car(CAR_BRAND, CAR_COLOR, CAR_LICENSE_PLATE);
    driver = new Driver(NAME, EMAIL, PHONE, car);
  }


  @Test
  @DisplayName("Should save driver and find by ID when not deleted")
  public void saveAndFindByIdAndDeletedFalse() {

    Driver saved = driverRepository.save(driver);

    Optional<Driver> found = driverRepository.findByIdAndDeletedFalse(saved.getId());

    assertThat(found.isPresent()).isTrue();
    assertThat(found.get().getName()).isEqualTo(NAME);
    assertThat(found.get().getEmail()).isEqualTo(EMAIL);
    assertThat(found.get().getPhone()).isEqualTo(PHONE);
    assertThat(found.get().getCar()).isEqualTo(car);
  }

  @Test
  @DisplayName("Should return empty Optional when driver is soft deleted")
  public void shouldReturnEmptyOptionalWhenDriverIsSoftDeleted() {

    Driver saved = driverRepository.save(driver);
    saved.markAsDeleted();
    driverRepository.save(saved);

    Optional<Driver> found = driverRepository.findByIdAndDeletedFalse(saved.getId());

    assertThat(found).isEmpty();
  }

  @Test
  @DisplayName("Should return true when checking existing non-deleted email")
  public void shouldUpdateDriver() {
    Driver saved = driverRepository.save(driver);

    saved.changeName(UPDATED_NAME);
    saved.changeEmail(UPDATED_EMAIL);

    Driver updated = driverRepository.save(saved);

    assertThat(updated.getName()).isEqualTo(UPDATED_NAME);
    assertThat(updated.getEmail()).isEqualTo(UPDATED_EMAIL);
    assertThat(updated.getPhone()).isEqualTo(PHONE);
    assertThat(updated.getCar()).isEqualTo(car);
  }

  @Test
  @DisplayName("Should return true when checking existing non-deleted phone")
  public void shouldSoftDeleteDriver() {

    Driver saved = driverRepository.save(driver);
    saved.markAsDeleted();
    driverRepository.save(saved);

    Optional<Driver> deleted =
            driverRepository.findById(saved.getId());

    assertThat(deleted).isPresent();
    assertThat(deleted.get().isDeleted()).isTrue();

    Optional<Driver> active =
            driverRepository.findByIdAndDeletedFalse(saved.getId());

    assertThat(active).isEmpty();
  }


  @Test
  @DisplayName("Should return false when checking non-existent email")
  public void shouldReturnTrueWhenEmailExistsAndNotDeleted() {

    driverRepository.save(driver);

    boolean exists = driverRepository.existsByEmailAndDeletedFalse(EMAIL);

    assertThat(exists).isTrue();

  }

  @Test
  @DisplayName("Should return false when checking email of deleted driver")
  public void shouldReturnTrueWhenPhoneExistsAndNotDeleted() {

    driverRepository.save(driver);

    boolean exists = driverRepository.existsByPhoneAndDeletedFalse(PHONE);

    assertThat(exists).isTrue();

  }

  @Test
  @DisplayName("Should return false when checking car plate of deleted driver")
  public void shouldReturnTrueWhenCarPlateExistsAndNotDeleted() {

    driverRepository.save(driver);

    boolean exists = carRepository.existsByLicensePlate(CAR_LICENSE_PLATE);

    assertThat(exists).isTrue();
  }
}