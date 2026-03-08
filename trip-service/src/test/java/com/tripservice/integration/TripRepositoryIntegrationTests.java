//package com.tripservice.integration;
//
//import com.tripservice.model.Address;
//import com.tripservice.model.Trip;
//import com.tripservice.model.enums.TripStatus;
//import com.tripservice.repository.TripRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@Testcontainers
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//public class TripRepositoryIntegrationTests {
//
//  @Container
//  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
//          .withDatabaseName("test")
//          .withUsername("root")
//          .withPassword("toot");
//
//  @DynamicPropertySource
//  static void configureProperties(DynamicPropertyRegistry registry) {
//    registry.add("spring.datasource.url", postgres::getJdbcUrl);
//    registry.add("spring.datasource.password", postgres::getPassword);
//    registry.add("spring.datasource.username", postgres::getUsername);
//  }
//
//  @Autowired
//  TripRepository tripRepository;
//
//  private Trip trip;
//  private Address pickupAddress;
//  private Address destinationAddress;
//
//  @BeforeEach
//  void setUp() {
//
//    tripRepository.deleteAll();
//
//    pickupAddress = new Address("Минск", "Ленина", "10");
//    destinationAddress = new Address("Минск", "Независимости", "50");
//
//    trip = Trip.builder()
//            .driverId(1L)
//            .passengerId(2L)
//            .pickupAddress(pickupAddress)
//            .destinationAddress(destinationAddress)
//            .status(TripStatus.CREATED)
//            .orderDateTime(LocalDateTime.now())
//            .price(BigDecimal.valueOf(25.50))
//            .build();
//
//  }
//
//  @Test
//  void saveAndFindById() {
//
//    Trip saved = tripRepository.save(trip);
//    Optional<Trip> found = tripRepository.findById(saved.getId());
//
//    assertThat(found.isPresent()).isTrue();
//    assertThat(found.get().getDriverId()).isEqualTo(trip.getDriverId());
//    assertThat(found.get().getPassengerId()).isEqualTo(trip.getPassengerId());
//    assertThat(found.get().getPickupAddress()).isEqualTo(trip.getPickupAddress());
//    assertThat(found.get().getDestinationAddress()).isEqualTo(trip.getDestinationAddress());
//    assertThat(found.get().getStatus()).isEqualTo(trip.getStatus());
//    assertThat(found.get().getOrderDateTime()).isEqualTo(trip.getOrderDateTime());
//    assertThat(found.get().getPrice()).isEqualTo(trip.getPrice());
//  }
//
//  @Test
//  void updateTripStatus() {
//
//    Trip saved = tripRepository.save(trip);
//
//    saved.setStatus(TripStatus.ACCEPTED);
//    Trip updated = tripRepository.save(saved);
//
//    assertThat(updated.getStatus()).isEqualTo(TripStatus.ACCEPTED);
//    assertThat(updated.getDriverId()).isEqualTo(1L);
//  }
//
//  @Test
//  void deleteByIdTrip() {
//
//    Trip saved = tripRepository.save(trip);
//
//    tripRepository.delete(saved);
//
//    Optional<Trip> found = tripRepository.findById(saved.getId());
//    assertThat(found.isPresent()).isFalse();
//  }
//
//
//  @Test
//  void existsById() {
//
//    Trip savedTrip = tripRepository.save(trip);
//
//    boolean exists = tripRepository.existsById(savedTrip.getId());
//
//    assertThat(exists).isTrue();
//  }
//
//
//  @Test
//  void findAll() {
//
//    Trip trip1 = tripRepository.save(trip);
//
//    Trip trip2 = Trip.builder()
//            .driverId(3L)
//            .passengerId(4L)
//            .pickupAddress(pickupAddress)
//            .destinationAddress(destinationAddress)
//            .status(TripStatus.ACCEPTED)
//            .price(BigDecimal.valueOf(35.00))
//            .build();
//    trip2 = tripRepository.save(trip2);
//
//
//    Iterable<Trip> allTrips = tripRepository.findAll();
//
//    assertThat(allTrips).hasSize(2);
//    assertThat(allTrips).extracting(Trip::getId)
//            .containsExactlyInAnyOrder(trip1.getId(), trip2.getId());
//  }
//
//  @Test
//  void count_ShouldReturnCorrectNumber() {
//
//    tripRepository.save(trip);
//    tripRepository.save(Trip.builder()
//            .driverId(3L)
//            .passengerId(4L)
//            .pickupAddress(pickupAddress)
//            .destinationAddress(destinationAddress)
//            .status(TripStatus.ACCEPTED)
//            .price(BigDecimal.valueOf(35.00))
//            .build());
//
//    long count = tripRepository.count();
//
//    assertThat(count).isEqualTo(2);
//  }
//
//}