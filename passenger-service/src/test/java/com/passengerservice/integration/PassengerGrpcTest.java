package com.passengerservice.integration;

import com.passengerservice.model.Passenger;
import com.passengerservice.repository.PassengerRepository;
import com.taxi.grpc.passenger.PassengerIdRequest;
import com.taxi.grpc.passenger.PassengerResponse;
import com.taxi.grpc.passenger.PassengerServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PassengerGrpcTest extends TestConfig {

  private static final String NAME = "John Doe";
  private static final String EMAIL = "john.doe@example.com";
  private static final String PHONE = "+375291234567";
  private static final int NON_EXISTENT_ID = 999;

  private long ID = 1;
  static ManagedChannel channel;
  static PassengerServiceGrpc.PassengerServiceBlockingStub stub;

  @Autowired
  PassengerRepository passengerRepository;

  @BeforeEach
  void setup() {
    passengerRepository.deleteAll();
    Passenger passenger = new Passenger(NAME, EMAIL, PHONE);
    passengerRepository.save(passenger);
    this.ID = passenger.getId();
  }

  @BeforeAll
  static void setUp() {
    channel = ManagedChannelBuilder
            .forAddress("localhost", 9015)
            .usePlaintext()
            .build();
    stub = PassengerServiceGrpc.newBlockingStub(channel);

  }

  @AfterAll
  static void teardown() {
    if (channel != null && !channel.isShutdown()) {
      channel.shutdownNow();
    }
  }

  @Test
  @DisplayName("Should return passenger")
  public void shouldReturnPassenger() {
    PassengerIdRequest request = PassengerIdRequest.newBuilder()
            .setPassengerId(ID)
            .build();

    PassengerResponse response = stub.getPassenger(request);

    assertThat(response.getId()).isEqualTo(ID);
    assertThat(response.getName()).isNotEmpty();
  }


  @Test
  @DisplayName("Should throw exception when passenger not found")
  public void shouldThrowExceptionWhenPassengerNotFound() {

    PassengerIdRequest request = PassengerIdRequest.newBuilder()
            .setPassengerId(NON_EXISTENT_ID)
            .build();

    assertThatThrownBy(() -> stub.getPassenger(request))
            .isInstanceOf(io.grpc.StatusRuntimeException.class)
            .hasMessageContaining("NOT_FOUND");
  }
}