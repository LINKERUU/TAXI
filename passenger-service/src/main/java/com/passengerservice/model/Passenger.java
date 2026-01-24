package com.passengerservice.model;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="passengers")
public class Passenger {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "email", unique = true, nullable = false)
  private String email;

  @Column(name = "phone", nullable = false) 
  private String phone;

  @Builder.Default
  @Column(name = "deleted", nullable = false)
  private boolean deleted = false;
}