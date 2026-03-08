package com.tripservice.model;

import com.tripservice.model.enums.TripStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "trips")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trip {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "driver_id", nullable = false)
  private Long driverId;

  @Column(name = "passenger_id", nullable = false)
  private Long passengerId;

  @Embedded
  @AttributeOverrides({
          @AttributeOverride(name = "city", column = @Column(name = "pickup_city", nullable = false)),
          @AttributeOverride(name = "street", column = @Column(name = "pickup_street", nullable = false)),
          @AttributeOverride(name = "buildingNumber", column = @Column(name = "pickup_building", nullable = false))
  })
  private Address pickupAddress;

  @Embedded
  @AttributeOverrides({
          @AttributeOverride(name = "city", column = @Column(name = "destination_city", nullable = false)),
          @AttributeOverride(name = "street", column = @Column(name = "destination_street", nullable = false)),
          @AttributeOverride(name = "buildingNumber", column = @Column(name = "destination_building", nullable = false)),
  })
  private Address destinationAddress;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private TripStatus status;

  @Column(name = "order_date_time", nullable = false)
  @CreationTimestamp
  private LocalDateTime orderDateTime;

  @Column(name = "price", nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  public Trip(Long driverId, Long passengerId,
              Address pickupAddress, Address destinationAddress,
              BigDecimal price) {

    this.driverId = driverId;
    this.passengerId = passengerId;
    this.pickupAddress = pickupAddress;
    this.destinationAddress = destinationAddress;
    this.price = price;
    this.status = TripStatus.CREATED;
    this.orderDateTime = LocalDateTime.now();
  }

  public void changeDriverId(Long driverId) {
    this.driverId = driverId;
  }

  public void changePickupAddress(Address pickupAddress) {
    this.pickupAddress = pickupAddress;
  }

  public void changeDestinationAddress(Address destinationAddress) {
    this.destinationAddress = destinationAddress;
  }

  public void changeStatus(TripStatus status) {
    this.status = status;
  }

  public void changePrice(BigDecimal price) {
    this.price = price;
  }
}
