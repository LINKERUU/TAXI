package com.passengerservice.integration;

import com.passengerservice.model.Passenger;
import com.passengerservice.repository.PassengerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PassengerRepositoryTest extends TestConfig {

  //Вынести отдельно реальзицию тестов и конфиг среды тестирования
  //Заменить волшебные переменные и добавить модификаторы доступа

  private static final String NAME = "John Doe";
  private static final String EMAIL = "john.doe@gmail.com";
  private static final String PHONE = "+375447743555";

  private static final String UPDATED_NAME = "John Updated";
  private static final String UPDATED_EMAIL = "genri.long@gmail.com";


  @Autowired
  private PassengerRepository passengerRepository;

  private Passenger passenger;

  @BeforeEach
  public void setUp() {
    passenger = new Passenger(NAME, EMAIL, PHONE);
  }


  @Test
  @DisplayName("Should save passenger and find by ID when not deleted")
  public void saveAndFindByIdAndDeletedFalse() {

    Passenger saved = passengerRepository.save(passenger);

    Optional<Passenger> found = passengerRepository.findByIdAndDeletedFalse(saved.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getName()).isEqualTo(saved.getName());
    assertThat(found.get().getEmail()).isEqualTo(saved.getEmail());
    assertThat(found.get().getPhone()).isEqualTo(saved.getPhone());
    assertThat(found.get().isDeleted()).isFalse();

  }


  @Test
  @DisplayName("Should return empty Optional when passenger is soft deleted")
  public void shouldReturnEmptyWhenPassengerDeleted() {

    Passenger saved = passengerRepository.save(passenger);
    saved.markAsDeleted();
    passengerRepository.save(saved);

    Optional<Passenger> found = passengerRepository.findByIdAndDeletedFalse(passenger.getId());

    assertThat(found).isEmpty();
  }

  @Test
  @DisplayName("Should return true when checking existing non-deleted email")
  public void shouldUpdatePassenger() {
    Passenger saved = passengerRepository.save(passenger);

    saved.changeName(UPDATED_NAME);
    saved.changeEmail(UPDATED_EMAIL);

    Passenger updated = passengerRepository.save(saved);

    assertThat(updated.getName()).isEqualTo(UPDATED_NAME);
    assertThat(updated.getEmail()).isEqualTo(UPDATED_EMAIL);
    assertThat(updated.getPhone()).isEqualTo(PHONE);
  }


  @Test
  @DisplayName("Should return true when checking existing non-deleted phone")
  public void shouldSoftDeletePassenger() {

    Passenger saved = passengerRepository.save(passenger);
    saved.markAsDeleted();
    passengerRepository.save(saved);

    Optional<Passenger> deleted =
            passengerRepository.findById(saved.getId());

    assertThat(deleted).isPresent();
    assertThat(deleted.get().isDeleted()).isTrue();

    Optional<Passenger> active =
            passengerRepository.findByIdAndDeletedFalse(saved.getId());

    assertThat(active).isEmpty();
  }


  @Test
  @DisplayName("Should return false when checking non-existent email")
  public void shouldReturnTrueWhenEmailExistsAndNotDeleted() {

    Passenger saved = passengerRepository.save(passenger);
    passengerRepository.save(saved);

    boolean exists = passengerRepository.existsByEmailAndDeletedFalse(EMAIL);

    assertThat(exists).isTrue();

  }

  @Test
  @DisplayName("Should return false when checking email of deleted passenger")
  public void shouldReturnTrueWhenPhoneExistsAndNotDeleted() {

    Passenger saved = passengerRepository.save(passenger);
    passengerRepository.save(saved);

    boolean exists = passengerRepository.existsByPhoneAndDeletedFalse(PHONE);

    assertThat(exists).isTrue();

  }
}