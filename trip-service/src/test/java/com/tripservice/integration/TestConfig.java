package com.tripservice.integration;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class TestConfig {
  private static final String POSTGRES_IMAGE = "postgres:15-alpine";
  private static final String DB_NAME = "trip_db";
  private static final String USERNAME = "admin";
  private static final String PASSWORD = "test";

  static final PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>(POSTGRES_IMAGE)
          .withDatabaseName(DB_NAME)
          .withUsername(USERNAME)
          .withPassword(PASSWORD);

  static {
    POSTGRES_CONTAINER.start();
  }

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
    registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
  }
}

