package com.passengerservice.integration;

import com.passengerservice.model.Passenger;
import com.passengerservice.repository.PassengerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PassengerRepositoryIntegrationTest {

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
          .withDatabaseName("testdatabase")
          .withUsername("testusername")
          .withPassword("testpassword");

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired
  private PassengerRepository passengerRepository;

  private Passenger passenger;

  @BeforeEach
  void setUp() {
    passengerRepository.deleteAll();

    passenger = Passenger.builder()
            .name("Иван Петров")
            .email("ivan.petrov@example.com")
            .phone("+375291234567")
            .deleted(false)
            .build();
  }

  @Test
  void saveAndFindById() {
    Passenger savedPassenger = passengerRepository.save(passenger);
    Optional<Passenger> foundPassenger = passengerRepository.findById(savedPassenger.getId());

    assertThat(foundPassenger).isPresent();
    assertThat(foundPassenger.get().getName()).isEqualTo("Иван Петров");
    assertThat(foundPassenger.get().getEmail()).isEqualTo("ivan.petrov@example.com");
    assertThat(foundPassenger.get().getPhone()).isEqualTo("+375291234567");
    assertThat(foundPassenger.get().isDeleted()).isFalse();
  }


  @Test
  void findByIdAndDeletedFalse_WhenNotDeleted_ReturnsPassenger() {
    // Given
    Passenger saved = passengerRepository.save(passenger);

    // When
    Optional<Passenger> found = passengerRepository.findByIdAndDeletedFalse(saved.getId());

    // Then
    assertThat(found).isPresent();
    assertThat(found.get().getId()).isEqualTo(saved.getId());
    assertThat(found.get().isDeleted()).isFalse();
  }

  @Test
  void findByIdAndDeletedFalse_WhenDeleted_ReturnsEmpty() {

    Passenger saved = passengerRepository.save(passenger);
    saved.setDeleted(true);
    passengerRepository.save(saved);


    Optional<Passenger> found = passengerRepository.findByIdAndDeletedFalse(saved.getId());

    assertThat(found).isEmpty();
  }


  @Test
  void updatePassenger() {

    Passenger saved = passengerRepository.save(passenger);


    saved.setName("Иван Петров Updated");
    saved.setPhone("+375299876543");
    Passenger updated = passengerRepository.save(saved);


    assertThat(updated.getName()).isEqualTo("Иван Петров Updated");
    assertThat(updated.getPhone()).isEqualTo("+375299876543");
    assertThat(updated.getEmail()).isEqualTo("ivan.petrov@example.com"); // unchanged
  }

  @Test
  void softDeletePassenger() {
    // Given
    Passenger saved = passengerRepository.save(passenger);

    // When
    saved.setDeleted(true);
    passengerRepository.save(saved);

    // Then
    Optional<Passenger> deleted = passengerRepository.findById(saved.getId());
    assertThat(deleted).isPresent();
    assertThat(deleted.get().isDeleted()).isTrue();

    Optional<Passenger> active = passengerRepository.findByIdAndDeletedFalse(saved.getId());
    assertThat(active).isEmpty();
  }

}