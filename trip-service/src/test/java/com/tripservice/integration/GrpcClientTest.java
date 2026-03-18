package com.tripservice.integration;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.taxi.grpc.driver.DriverServiceGrpc;
import com.taxi.grpc.passenger.PassengerResponse;
import com.taxi.grpc.passenger.PassengerServiceGrpc;
import com.tripservice.client.grpc.DriverGrpcClient;
import com.tripservice.client.grpc.PassengerGrpcClient;
import com.tripservice.exception.custom.ServiceUnavailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.wiremock.grpc.GrpcExtensionFactory;
import org.wiremock.grpc.dsl.WireMockGrpc;
import org.wiremock.grpc.dsl.WireMockGrpcService;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.wiremock.grpc.dsl.WireMockGrpc.message;
import static org.wiremock.grpc.dsl.WireMockGrpc.method;

@SpringBootTest
@ActiveProfiles("test")
class GrpcClientTest extends TestConfig {

  private static final long DRIVER_ID = 1;
  private static final long PASSENGER_ID = 1;

  WireMockGrpcService passengerService;
  WireMockGrpcService driverService;

  @Autowired
  DriverGrpcClient driverClient;

  @Autowired
  PassengerGrpcClient passengerClient;

  @RegisterExtension
  static WireMockExtension wm =
          WireMockExtension.newInstance()
                  .options(
                          wireMockConfig()
                                  .dynamicPort()
                                  .extensions(new GrpcExtensionFactory())
                  )
                  .build();

  @DynamicPropertySource
  static void registerProps(DynamicPropertyRegistry registry) {
    registry.add(
            "spring.grpc.client.channels.passenger-service.address",
            () -> "localhost:" + wm.getPort()
    );
    registry.add("spring.grpc.client.channels.driver-service.address",
            () -> "localhost:" + wm.getPort()
    );
  }

  @BeforeEach
  void setUp() {
    passengerService = new WireMockGrpcService(
            new WireMock(wm.getPort()),
            PassengerServiceGrpc.SERVICE_NAME
    );

    driverService = new WireMockGrpcService(
            new WireMock(wm.getPort()),
            DriverServiceGrpc.SERVICE_NAME
    );
  }

  @Test
  public void shouldValidatePassenger() {

    passengerService.stubFor(
            method("GetPassenger")
                    .willReturn(
                            message(
                                    PassengerResponse.newBuilder()
                                            .setId(PASSENGER_ID)
                                            .build()
                            )
                    )
    );

    passengerClient.existsPassenger(PASSENGER_ID);
  }

  @Test
  public void shouldPassengerFallbackWhenServiceUnavailable() {

    passengerService.stubFor(
            method("GetPassenger")
                    .willReturn(WireMockGrpc.Status.UNAVAILABLE, "Service down")
    );

    assertThrows(ServiceUnavailableException.class,
            () -> passengerClient.existsPassenger(PASSENGER_ID));
  }

  @Test
  public void shouldValidateDriver() {

    driverService.stubFor(
            method("GetDriver")
                    .willReturn(
                            message(
                                    PassengerResponse.newBuilder()
                                            .setId(DRIVER_ID)
                                            .build()
                            )
                    )
    );

    driverClient.existsDriver(DRIVER_ID);
  }

  @Test
  public void shouldDriverFallbackWhenServiceUnavailable() {

    driverService.stubFor(
            method("GetDriver")
                    .willReturn(WireMockGrpc.Status.UNAVAILABLE, "Service down")
    );

    assertThrows(ServiceUnavailableException.class,
            () -> driverClient.existsDriver(DRIVER_ID));
  }

}