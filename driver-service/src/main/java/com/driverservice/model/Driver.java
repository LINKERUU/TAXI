package com.driverservice.model;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="drivers")
public class Driver {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "email", unique = true, nullable = false)
  private String email;

  @Column(name = "phone",unique = true, nullable = false)
  private String phone;

  @Builder.Default
  @Column(name = "deleted", nullable = false)
  private boolean deleted = false;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "car_id",nullable = false)
  private Car car;

}