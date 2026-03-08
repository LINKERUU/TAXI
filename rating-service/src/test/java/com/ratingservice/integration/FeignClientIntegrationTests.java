//package com.ratingservice.integration;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.github.tomakehurst.wiremock.client.WireMock;
//import com.ratingservice.client.TripClient;
//import com.ratingservice.repository.RatingRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.GenericContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import org.testcontainers.utility.DockerImageName;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//import static com.github.tomakehurst.wiremock.client.WireMock.*;
//import static org.assertj.core.api.Assertions.assertThat;
//
//@Slf4j
//@SpringBootTest
//@Testcontainers
//class FeignClientIntegrationTests {
//
//  @Container
//  static GenericContainer<?> wiremockContainer = new GenericContainer<>(
//          DockerImageName.parse("wiremock/wiremock:2.35.0"))
//          .withExposedPorts(8080);
//
//  private static WireMock wireMockClient;
//
//  @Autowired
//  private TripClient tripClient;
//
//  @Autowired
//  private ObjectMapper objectMapper;
//
//  @MockitoBean
//  private RatingRepository ratingRepository;
//
//  @DynamicPropertySource
//  static void dynamicProperties(DynamicPropertyRegistry registry) {
//    String wiremockUrl = String.format("http://%s:%d",
//            wiremockContainer.getHost(),
//            wiremockContainer.getMappedPort(8080));
//
//    log.info("WireMock URL: {}", wiremockUrl);
//
//    registry.add("spring.cloud.openfeign.client.config.trip-service.url", () -> wiremockUrl);
//
//    registry.add("spring.liquibase.enabled", () -> "false");
//    registry.add("spring.data.jpa.repositories.enabled", () -> "false");
//    registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
//    registry.add("spring.autoconfigure.exclude", () ->
//            "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
//                    "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration," +
//                    "org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration," +
//                    "org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration," +
//                    "org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration," +
//                    "org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration");
//
//    registry.add("spring.kafka.bootstrap-servers", () -> "localhost:9999");
//
//    registry.add("eureka.client.enabled", () -> "false");
//    registry.add("spring.cloud.discovery.enabled", () -> "false");
//    registry.add("spring.cloud.loadbalancer.enabled", () -> "false");
//    registry.add("spring.cloud.service-registry.auto-registration.enabled", () -> "false");
//
//    registry.add("spring.main.allow-bean-definition-overriding", () -> "true");
//  }
//
//  @BeforeEach
//  void setUp() {
//    String host = wiremockContainer.getHost();
//    int port = wiremockContainer.getMappedPort(8080);
//
//    WireMock.configureFor(host, port);
//
//    if (wireMockClient == null) {
//      wireMockClient = new WireMock(host, port);
//    }
//
//    wireMockClient.resetMappings();
//    wireMockClient.resetRequests();
//  }
//
//  @Test
//  void getTripById_success() throws Exception {
//    long tripId = 1L;
//
//    TripClient.TripResponse expected = new TripClient.TripResponse(
//            1L, 100L, 200L,
//            "Минск, Ленина 10",
//            "Минск, Независимости 50",
//            TripClient.TripStatus.COMPLETED,
//            LocalDateTime.parse("2026-02-19T10:00:00"),
//            new BigDecimal("25.50")
//    );
//
//    wireMockClient.register(
//            get(urlEqualTo("/api/trips/" + tripId))
//                    .willReturn(aResponse()
//                            .withStatus(200)
//                            .withHeader("Content-Type", "application/json")
//                            .withBody(objectMapper.writeValueAsString(expected)))
//    );
//
//    TripClient.TripResponse result = tripClient.getTripById(tripId);
//
//    assertThat(result).isNotNull();
//    assertThat(result.id()).isEqualTo(1L);
//    assertThat(result.driverId()).isEqualTo(100L);
//    assertThat(result.passengerId()).isEqualTo(200L);
//    assertThat(result.status()).isEqualTo(TripClient.TripStatus.COMPLETED);
//    assertThat(result.price()).isEqualByComparingTo("25.50");
//
//    verify(1, getRequestedFor(urlEqualTo("/api/trips/" + tripId)));
//  }
//}