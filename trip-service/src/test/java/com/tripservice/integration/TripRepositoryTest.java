package com.tripservice.integration;

import com.tripservice.model.Address;
import com.tripservice.model.Trip;
import com.tripservice.repository.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TripRepositoryTest extends TestConfig {

  private static final Long DRIVER_ID = 1L;
  private static final Long PASSENGER_ID = 1L;

  private static final String PICKUP_CITY = "Минск";
  private static final String PICKUP_STREET = "Л.Беды";
  private static final String PICKUP_NUMBER = "4";
  private static final String DESTINATION_CITY = "Минск";
  private static final String DESTINATION_STREET = "Ленина";
  private static final String DESTINATION_NUMBER = "7";
  private static final BigDecimal PRICE = BigDecimal.valueOf(10.84);

  private static final BigDecimal UPDATE_PRICE = BigDecimal.valueOf(15.84);
  private static final Long UPDATED_DRIVER_ID = 2L;
  private static final String UPDATED_PICKUP_CITY = "Минск";
  private static final String UPDATED_PICKUP_STREET = "Комсомольская";
  private static final String UPDATED_PICKUP_NUMBER = "26";
  private static final String UPDATED_DESTINATION_CITY = "Минск";
  private static final String UPDATED_DESTINATION_STREET = "Пушкина";
  private static final String UPDATED_DESTINATION_NUMBER = "77";

  @Autowired
  TripRepository tripRepository;

  private Trip trip;


  @BeforeEach
  public void setUp() {
    trip = new Trip(DRIVER_ID, PASSENGER_ID, new Address(PICKUP_CITY, PICKUP_STREET, PICKUP_NUMBER), new Address(DESTINATION_CITY, DESTINATION_STREET, DESTINATION_NUMBER), PRICE);
  }


  @Test
  @DisplayName("Should save trip and find by ID")
  public void saveAndFindById() {

    Trip saved = tripRepository.save(trip);

    Optional<Trip> found = tripRepository.findById(saved.getId());

    assertThat(found).isPresent();
    assertThat(found.get()).isEqualTo(trip);
    assertThat(found.get().getDriverId()).isEqualTo(DRIVER_ID);
    assertThat(found.get().getPassengerId()).isEqualTo(PASSENGER_ID);
  }


  @Test
  @DisplayName("Should return empty Optional when trip is soft deleted")
  public void shouldReturnEmptyWhenTripDeleted() {

    Trip saved = tripRepository.save(trip);
    tripRepository.delete(saved);

    Optional<Trip> found = tripRepository.findById(saved.getId());

    assertThat(found).isEmpty();
  }

  @Test
  @DisplayName("Should update trip")
  public void shouldUpdateTrip() {
    Trip saved = tripRepository.save(trip);

    saved.changeDestinationAddress(new Address(UPDATED_DESTINATION_CITY, UPDATED_DESTINATION_STREET, UPDATED_DESTINATION_NUMBER));
    saved.changePickupAddress(new Address(UPDATED_PICKUP_CITY, UPDATED_PICKUP_STREET, UPDATED_PICKUP_NUMBER));
    saved.changeDriverId(UPDATED_DRIVER_ID);
    saved.changePrice(UPDATE_PRICE);

    Trip updated = tripRepository.save(saved);

    assertThat(updated.getPrice()).isEqualTo(UPDATE_PRICE);
    assertThat(updated.getDestinationAddress().getCity()).isEqualTo(UPDATED_DESTINATION_CITY);
    assertThat(updated.getDestinationAddress().getStreet()).isEqualTo(UPDATED_DESTINATION_STREET);
    assertThat(updated.getDestinationAddress().getBuildingNumber()).isEqualTo(UPDATED_DESTINATION_NUMBER);
    assertThat(updated.getPickupAddress().getCity()).isEqualTo(UPDATED_PICKUP_CITY);
    assertThat(updated.getPickupAddress().getStreet()).isEqualTo(UPDATED_PICKUP_STREET);
    assertThat(updated.getPickupAddress().getBuildingNumber()).isEqualTo(UPDATED_PICKUP_NUMBER);
    assertThat(updated.getDriverId()).isEqualTo(UPDATED_DRIVER_ID);
  }


  @Test
  @DisplayName("Should delete trip")
  public void shouldDeleteTrip() {

    Trip saved = tripRepository.save(trip);
    tripRepository.delete(saved);

    Optional<Trip> deleted =
            tripRepository.findById(saved.getId());

    assertThat(deleted).isEmpty();
  }

}