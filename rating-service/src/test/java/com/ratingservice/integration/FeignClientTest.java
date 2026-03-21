package com.ratingservice.integration;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.ratingservice.client.TripServiceClient;
import com.ratingservice.client.dto.TripResponse;
import com.ratingservice.exception.custom.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@Testcontainers
class FeignClientTest extends TestConfig {

  private static final long TRIP_ID = 1;
  private static final String BASE_URL = "/api/trips/";
  private static final String STATUS = "COMPLETED";

  @Autowired
  TripServiceClient tripServiceClient;

  @RegisterExtension
  static WireMockExtension wm = WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();

  @DynamicPropertySource
  static void registerProps(DynamicPropertyRegistry registry) {
    registry.add("spring.cloud.openfeign.client.config.trip-service.url", () -> "http://localhost:" + wm.getPort());
  }


  @Test
  void shouldReturnTripSuccessfully() {

    wm.stubFor(get(urlEqualTo(BASE_URL + TRIP_ID)).willReturn(aResponse().withHeader("Content-Type", "application/json").withBodyFile("trip-response.json")));

    TripResponse response = tripServiceClient.getTripById(TRIP_ID);

    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo(TRIP_ID);
    assertThat(response.status().name()).isEqualTo(STATUS);
  }

  @Test
  void shouldTriggerFallbackWhenServiceDown() {

    wm.stubFor(get(urlEqualTo(BASE_URL + TRIP_ID)).willReturn(serverError()));

    assertThrows(ServiceUnavailableException.class, () -> tripServiceClient.getTripById(TRIP_ID));
  }
}