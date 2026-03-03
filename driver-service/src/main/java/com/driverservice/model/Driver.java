package com.driverservice.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="drivers")
public class Driver {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "phone", nullable = false)
  private String phone;

  @Column(name = "deleted", nullable = false)
  private boolean deleted = false;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "car_id",nullable = false)
  private Car car;

  public Driver(String name, String email, String phone, Car car) {
    this.name = name;
    this.email = email;
    this.phone = phone;
    this.deleted = false;
    setCar(car);
  }

  private void setCar(Car car) {
    this.car = car;
    car.setDriver(this);
  }

  public void markAsDeleted() {
    this.deleted = true;
  }

  public void changeName(String name) {
    this.name = name;
  }

  public void changeEmail(String email) {
    this.email = email;
  }

  public void changePhone(String phone) {
    this.phone = phone;
  }

}

