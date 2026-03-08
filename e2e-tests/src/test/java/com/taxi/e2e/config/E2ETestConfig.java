package com.taxi.e2e.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class E2ETestConfig {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public String baseUrl() {
    return "http://localhost:8083"; // Укажи правильный порт твоего сервиса
  }
}