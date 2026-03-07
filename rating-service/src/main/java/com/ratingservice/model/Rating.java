package com.ratingservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;


@Entity
@Table(name = "ratings", uniqueConstraints = @UniqueConstraint(
   columnNames = {"trip_id", "rater_type"}
))
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "trip_id", nullable = false)
  @NotNull(message = "Trip ID is required")
  private Long tripId;

  @Enumerated(EnumType.STRING)
  @Column(name = "rater_type", nullable = false)
  @NotNull(message = "Rater type is required")
  private RaterType raterType;

  @Column(nullable = false)
  @Min(value = 1, message = "Rating must be at least 1")
  @Max(value = 5, message = "Rating must be at most 5")
  private Integer score;

  @Column(length = 1000)
  @Size(max = 1000, message = "Comment must not exceed 1000 characters")
  private String comment;

  public enum RaterType {
    DRIVER,
    PASSENGER
  }
}