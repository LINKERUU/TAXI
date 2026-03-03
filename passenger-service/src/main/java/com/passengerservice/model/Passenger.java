package com.passengerservice.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor
@Table(name="passengers")
public class Passenger {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "phone", nullable = false, length = 13)
  private String phone;

  @Column(name = "deleted", nullable = false)
  private boolean deleted = false;

  public Passenger(String name,String email,String phone) {
    this.name = name;
    this.email = email;
    this.phone = phone;
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



