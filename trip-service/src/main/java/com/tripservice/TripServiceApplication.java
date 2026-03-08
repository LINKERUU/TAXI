package com.tripservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableFeignClients
@EnableKafka
@EnableScheduling
public class TripServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(TripServiceApplication.class, args);
  }

}
