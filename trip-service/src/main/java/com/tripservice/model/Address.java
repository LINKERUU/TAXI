package com.tripservice.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

  @NotBlank
  private String city;

  @NotBlank
  private String street;

  @NotBlank
  private String buildingNumber;
}
