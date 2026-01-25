package com.tripservice.model;

import com.tripservice.model.enums.TripStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "trips")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "driver_id",nullable = false)
  @NotNull
  private Long driverId;

  @Column(name = "passenger_id",nullable = false)
  @NotNull
  private Long passengerId;

  @Embedded
  @AttributeOverrides({
          @AttributeOverride(name = "city", column = @Column(name = "pickup_city")),
          @AttributeOverride(name = "street", column = @Column(name = "pickup_street")),
          @AttributeOverride(name = "buildingNumber", column = @Column(name = "pickup_building"))
  })
  private Address pickupAddress;

  @Embedded
  @AttributeOverrides({
          @AttributeOverride(name = "city", column = @Column(name = "destination_city")),
          @AttributeOverride(name = "street", column = @Column(name = "destination_street")),
          @AttributeOverride(name = "buildingNumber", column = @Column(name = "destination_building"))
  })
  private Address destinationAddress;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private TripStatus status = TripStatus.CREATED;

  @Column(name = "order_date_time", nullable = false)
  @CreationTimestamp
  private LocalDateTime orderDateTime;

  @Column(precision = 10, scale = 2)
  @DecimalMin(value = "0.0", inclusive = false)
  private BigDecimal price;
}
