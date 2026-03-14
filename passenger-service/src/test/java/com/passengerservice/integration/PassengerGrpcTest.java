package com.passengerservice.integration;

import com.taxi.grpc.passenger.PassengerIdRequest;
import com.taxi.grpc.passenger.PassengerResponse;
import com.taxi.grpc.passenger.PassengerServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class PassengerGrpcTest {

  private static final int ID = 1;
  private static final int NON_EXISTENT_ID = 999;

  ManagedChannel channel;
  PassengerServiceGrpc.PassengerServiceBlockingStub stub;

  @BeforeEach
  void setup() {
    channel = ManagedChannelBuilder
            .forAddress("localhost", 9015)
            .usePlaintext()
            .build();
    stub = PassengerServiceGrpc.newBlockingStub(channel);
  }

  @AfterEach
  void teardown() {
    if (channel != null && !channel.isShutdown()) {
      channel.shutdownNow();
    }
  }

  @Test
  @DisplayName("Should return passenger")
  void shouldReturnPassenger() {
    PassengerIdRequest request = PassengerIdRequest.newBuilder()
            .setPassengerId(ID)
            .build();

    PassengerResponse response = stub.getPassenger(request);

    assertThat(response.getId()).isEqualTo(ID);
    assertThat(response.getName()).isNotEmpty();
  }
  

  @Test
  @DisplayName("Should throw exception when passenger not found")
  void shouldThrowExceptionWhenPassengerNotFound() {

    PassengerIdRequest request = PassengerIdRequest.newBuilder()
            .setPassengerId(NON_EXISTENT_ID)
            .build();
    
    assertThatThrownBy(() -> stub.getPassenger(request))
            .isInstanceOf(io.grpc.StatusRuntimeException.class)
            .hasMessageContaining("NOT_FOUND");
  }
}
