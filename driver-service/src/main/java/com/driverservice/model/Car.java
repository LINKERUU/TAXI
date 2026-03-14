package com.driverservice.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "cars")
public class Car {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "color", nullable = false)
  private String color;

  @Column(name = "brand", nullable = false)
  private String brand;

  @Column(name = "license_plate", unique = true, nullable = false)
  private String licensePlate;

  @OneToOne(mappedBy = "car")
  private Driver driver;

  public Car(String color, String brand, String licensePlate) {
    this.color = color;
    this.brand = brand;
    this.licensePlate = licensePlate;
  }

  void setDriver(Driver driver) {
    this.driver = driver;
  }

  public void changeColor(String color) {
    this.color = color;
  }

  public void changeBrand(String brand) {
    this.brand = brand;
  }

  public void changeLicensePlate(String licensePlate) {
    this.licensePlate = licensePlate;
  }
}