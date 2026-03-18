package com.tripservice.config;

import com.taxi.grpc.driver.DriverServiceGrpc;
import com.taxi.grpc.passenger.PassengerServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {

  @Bean(destroyMethod = "shutdown")
  public ManagedChannel driverChannel(@Value("${spring.grpc.client.channels.driver-service.address}") String address) {

    String[] parts = address.split(":");

    return ManagedChannelBuilder.forAddress(parts[0], Integer.parseInt(parts[1])).usePlaintext().build();
  }

  @Bean(destroyMethod = "shutdown")
  public ManagedChannel passengerChannel(@Value("${spring.grpc.client.channels.passenger-service.address}") String address) {

    String[] parts = address.split(":");

    return ManagedChannelBuilder.forAddress(parts[0], Integer.parseInt(parts[1])).usePlaintext().build();
  }

  @Bean
  public DriverServiceGrpc.DriverServiceBlockingStub driverStub(@Qualifier("driverChannel") ManagedChannel channel) {

    return DriverServiceGrpc.newBlockingStub(channel);
  }

  @Bean
  public PassengerServiceGrpc.PassengerServiceBlockingStub passengerStub(@Qualifier("passengerChannel") ManagedChannel channel) {

    return PassengerServiceGrpc.newBlockingStub(channel);
  }
}
