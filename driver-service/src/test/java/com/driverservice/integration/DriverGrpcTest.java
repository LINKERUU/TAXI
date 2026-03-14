package com.driverservice.integration;

import com.driverservice.model.Car;
import com.driverservice.model.Driver;
import com.driverservice.repository.CarRepository;
import com.driverservice.repository.DriverRepository;
import com.taxi.grpc.driver.DriverIdRequest;
import com.taxi.grpc.driver.DriverResponse;
import com.taxi.grpc.driver.DriverServiceGrpc;
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
public class DriverGrpcTest extends TestConfig {


  private static final int NON_EXISTENT_ID = 999;
  private static final String NAME = "Иван Петров";
  private static final String EMAIL = "ivan.petrov@example.com";
  private static final String PHONE = "+375291234567";
  private static final String CAR_BRAND = "Toyota";
  private static final String CAR_COLOR = "Черный";
  private static final String CAR_LICENSE_PLATE = "1234 AB-1";

  private long ID;
  static ManagedChannel channel;
  static DriverServiceGrpc.DriverServiceBlockingStub stub;

  @Autowired
  private DriverRepository driverRepository;

  @Autowired
  private CarRepository carRepository;

  @BeforeEach
  void setupData() {
    driverRepository.deleteAll();
    carRepository.deleteAll();
    Car car = new Car(CAR_BRAND, CAR_COLOR, CAR_LICENSE_PLATE);
    Driver driver = new Driver(NAME, EMAIL, PHONE, car);
    driverRepository.save(driver);
    this.ID = driver.getId();
  }

  @BeforeAll
  static void setUp() {
    channel = ManagedChannelBuilder
            .forAddress("localhost", 9016)
            .usePlaintext()
            .build();
    stub = DriverServiceGrpc.newBlockingStub(channel);
  }

  @AfterAll
  static void teardown() {
    if (channel != null && !channel.isShutdown()) {
      channel.shutdownNow();
    }
  }

  @Test
  @DisplayName("Should return driver")
  public void shouldReturnPassenger() {
    DriverIdRequest request = DriverIdRequest.newBuilder()
            .setDriverId(ID)
            .build();

    DriverResponse response = stub.getDriver(request);

    assertThat(response.getId()).isEqualTo(ID);
    assertThat(response.getName()).isNotEmpty();
  }


  @Test
  @DisplayName("Should throw exception when driver not found")
  public void shouldThrowExceptionWhenDriverNotFound() {

    DriverIdRequest request = DriverIdRequest.newBuilder()
            .setDriverId(NON_EXISTENT_ID)
            .build();

    assertThatThrownBy(() -> stub.getDriver(request))
            .isInstanceOf(io.grpc.StatusRuntimeException.class)
            .hasMessageContaining("NOT_FOUND");
  }
}
