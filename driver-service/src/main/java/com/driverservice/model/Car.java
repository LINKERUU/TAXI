package com.driverservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

  @Pattern(regexp="^[0-9]{4} [A-Z]{2}-[1-7]$",
          message = "Номер должен быть в формате: 1234 AB-1")
  @Column(name = "licensePlate", unique = true, nullable = false)
  private String licensePlate;

  @OneToOne(mappedBy = "car")
  private Driver driver;

}