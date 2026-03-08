//package com.tripservice.integration;
//
//import com.github.tomakehurst.wiremock.WireMockServer;
//import com.github.tomakehurst.wiremock.client.WireMock;
//import com.taxi.grpc.driver.DriverIdRequest;
//import com.taxi.grpc.driver.DriverResponse;
//import com.taxi.grpc.passenger.PassengerIdRequest;
//import com.taxi.grpc.passenger.PassengerResponse;
//import com.tripservice.client.grpc.DriverGrpcClient;
//import com.tripservice.client.grpc.PassengerGrpcClient;
//import io.grpc.ManagedChannel;
//import io.grpc.ManagedChannelBuilder;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Primary;
//import org.springframework.grpc.client.ChannelBuilderOptions;
//import org.springframework.grpc.client.GrpcChannelFactory;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
//import org.wiremock.grpc.GrpcExtensionFactory;
//import org.wiremock.grpc.dsl.WireMockGrpcService;
//
//import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.wiremock.grpc.dsl.WireMockGrpc.*;
//
//@SpringJUnitConfig(classes = {
//        GrpcClientIntegrationTests.GrpcClientTestConfig.class,
//        DriverGrpcClient.class,
//        PassengerGrpcClient.class
//})
//@ActiveProfiles("test")
//class GrpcClientIntegrationTests {
//
//  private static WireMockServer wireMockServer;
//  private static WireMockGrpcService driverGrpc;
//  private static WireMockGrpcService passengerGrpc;
//
//  @BeforeAll
//  static void startWireMock() {
//    wireMockServer = new WireMockServer(
//            wireMockConfig()
//                    .dynamicPort()
//                    .withRootDirectory("src/test/resources/wiremock")
//                    .extensions(new GrpcExtensionFactory())
//    );
//    wireMockServer.start();
//
//    WireMock wm = new WireMock("localhost", wireMockServer.port());
//
//    driverGrpc = new WireMockGrpcService(wm, "com.taxi.grpc.driver.DriverService");
//    passengerGrpc = new WireMockGrpcService(wm, "com.taxi.grpc.passenger.PassengerService");
//
//    System.out.println("WireMock gRPC сервер запущен на порту: " + wireMockServer.port());
//  }
//
//  @AfterAll
//  static void stopWireMock() {
//    if (wireMockServer != null && wireMockServer.isRunning()) {
//      wireMockServer.stop();
//    }
//  }
//
//  @TestConfiguration
//  @ComponentScan(basePackages = "com.tripservice.client.grpc")
//  static class GrpcClientTestConfig {
//
//    @Bean
//    @Primary
//    public GrpcChannelFactory testGrpcChannelFactory() {
//      return new GrpcChannelFactory() {
//        @Override
//        public boolean supports(String target) {
//          return true;
//        }
//
//        @Override
//        public ManagedChannel createChannel(String name) {
//          return ManagedChannelBuilder
//                  .forAddress("localhost", wireMockServer.port())
//                  .usePlaintext()
//                  .build();
//        }
//
//        @Override
//        public ManagedChannel createChannel(String target, ChannelBuilderOptions options) {
//          return createChannel(target);
//        }
//      };
//    }
//  }
//
//  @DynamicPropertySource
//  static void grpcClientProps(DynamicPropertyRegistry registry) {
//    registry.add("spring.grpc.client.enabled", () -> "false");
//    registry.add("eureka.client.enabled", () -> "false");
//    registry.add("spring.cloud.discovery.enabled", () -> "false");
//    registry.add("spring.cloud.service-registry.auto-registration.enabled", () -> "false");
//  }
//
//  @Autowired
//  private DriverGrpcClient driverGrpcClient;
//
//  @Autowired
//  private PassengerGrpcClient passengerGrpcClient;
//
//  @BeforeEach
//  void resetStubs() {
//    if (driverGrpc != null) {
//      driverGrpc.resetAll();
//    }
//    if (passengerGrpc != null) {
//      passengerGrpc.resetAll();
//    }
//  }
//
//  @Test
//  void validateDriver_success() {
//    long driverId = 1L;
//
//    DriverIdRequest request = DriverIdRequest.newBuilder()
//            .setDriverId(driverId)
//            .build();
//
//    DriverResponse response = DriverResponse.newBuilder()
//            .setId(driverId)
//            .setName("John Driver")
//            .setEmail("driver@test.com")
//            .setPhone("+100000000")
//            .setCarColor("Black")
//            .setCarBrand("Toyota")
//            .setCarLicensePlat("A123BC")
//            .build();
//
//    driverGrpc.stubFor(
//            method("GetDriver")
//                    .withRequestMessage(equalToMessage(request))
//                    .willReturn(message(response))
//    );
//
//    boolean result = driverGrpcClient.validateDriver(driverId);
//
//    assertThat(result).isTrue();
//    driverGrpc.verify(1, "GetDriver").withRequestMessage(equalToMessage(request));
//  }
//
//  @Test
//  void validateDriver_notFound() {
//    long driverId = 999L;
//
//    DriverIdRequest request = DriverIdRequest.newBuilder()
//            .setDriverId(driverId)
//            .build();
//
//    driverGrpc.stubFor(
//            method("GetDriver")
//                    .withRequestMessage(equalToMessage(request))
//                    .willReturn(org.wiremock.grpc.dsl.WireMockGrpc.Status.NOT_FOUND, "Driver not found")
//    );
//
//    boolean result = driverGrpcClient.validateDriver(driverId);
//
//    assertThat(result).isFalse();
//    driverGrpc.verify(1, "GetDriver").withRequestMessage(equalToMessage(request));
//  }
//
//  @Test
//  void validatePassenger_success() {
//    long passengerId = 2L;
//
//    PassengerIdRequest request = PassengerIdRequest.newBuilder()
//            .setPassengerId(passengerId)
//            .build();
//
//    PassengerResponse response = PassengerResponse.newBuilder()
//            .setId(passengerId)
//            .setName("Jane Passenger")
//            .setEmail("passenger@test.com")
//            .setPhone("+200000000")
//            .build();
//
//    passengerGrpc.stubFor(
//            method("GetPassenger")
//                    .withRequestMessage(equalToMessage(request))
//                    .willReturn(message(response))
//    );
//
//    boolean result = passengerGrpcClient.validatePassenger(passengerId);
//
//    assertThat(result).isTrue();
//    passengerGrpc.verify(1, "GetPassenger").withRequestMessage(equalToMessage(request));
//  }
//
//  @Test
//  void validatePassenger_notFound() {
//    long passengerId = 999L;
//
//    PassengerIdRequest request = PassengerIdRequest.newBuilder()
//            .setPassengerId(passengerId)
//            .build();
//
//    passengerGrpc.stubFor(
//            method("GetPassenger")
//                    .withRequestMessage(equalToMessage(request))
//                    .willReturn(org.wiremock.grpc.dsl.WireMockGrpc.Status.NOT_FOUND, "Passenger not found")
//    );
//
//    boolean result = passengerGrpcClient.validatePassenger(passengerId);
//
//    assertThat(result).isFalse();
//    passengerGrpc.verify(1, "GetPassenger").withRequestMessage(equalToMessage(request));
//  }
//}