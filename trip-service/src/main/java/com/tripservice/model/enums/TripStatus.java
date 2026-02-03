package com.tripservice.model.enums;

import lombok.RequiredArgsConstructor;


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


}
