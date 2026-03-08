package com.ratingservice.model;

import com.ratingservice.model.enums.RaterType;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "ratings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rating {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "trip_id", nullable = false)
  private Long tripId;

  @Enumerated(EnumType.STRING)
  @Column(name = "rater_type", nullable = false)
  private RaterType raterType;

  @Column(name = "score", nullable = false)
  private Integer score;

  @Column(name = "comment", length = 1000)
  private String comment;

  public Rating(Long tripId, RaterType raterType, Integer score, String comment) {
    this.tripId = tripId;
    this.raterType = raterType;
    this.score = score;
    this.comment = comment;
  }

  public void changeScore(Integer score) {
    this.score = score;
  }

  public void changeComment(String comment) {
    this.comment = comment;
  }
}



