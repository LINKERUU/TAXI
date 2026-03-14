package com.driverservice.mapper;

import com.driverservice.dto.DriverRequest;
import com.driverservice.dto.DriverResponse;
import com.driverservice.model.Car;
import com.driverservice.model.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DriverMapper {

  DriverResponse toDriverResponse(Driver driver);

  @Mapping(target = "car", source = ".")
  Driver toEntity(DriverRequest request);

  @Mapping(target = "color", source = "carColor")
  @Mapping(target = "brand", source = "carBrand")
  @Mapping(target = "licensePlate", source = "carLicensePlate")
  Car toCar(DriverRequest request);
}