package com.tripservice.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TripStatus {
  CREATED("Создана"),
  ACCEPTED("Принята"),
  DRIVER_EN_ROUTE("В пути за пассажиром"),
  PASSENGER_ON_BOARD("Пассажир в машине"),
  IN_PROGRESS("В пути к месту назначения"),
  COMPLETED("Завершена"),
  CANCELLED("Отменена");
  private final String description;

  public static boolean canTransitionTo(TripStatus current,TripStatus next) {
    return switch (current) {
      case CREATED -> next == ACCEPTED || next == CANCELLED;
      case ACCEPTED -> next == DRIVER_EN_ROUTE || next == CANCELLED;
      case DRIVER_EN_ROUTE -> next == PASSENGER_ON_BOARD || next == CANCELLED;
      case PASSENGER_ON_BOARD -> next == IN_PROGRESS || next == CANCELLED;
      case IN_PROGRESS -> next == COMPLETED || next == CANCELLED;
      case COMPLETED, CANCELLED -> false;
    };
  }
}
