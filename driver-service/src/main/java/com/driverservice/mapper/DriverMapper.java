package com.driverservice.mapper;

import com.driverservice.dto.CarResponse;
import com.driverservice.dto.DriverRequest;
import com.driverservice.dto.DriverResponse;
import com.driverservice.model.Car;
import com.driverservice.model.Driver;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface DriverMapper {

  @Mapping(target = "car", source = "car")
  DriverResponse toDriverResponse(Driver driver);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "car", source = "request")
  @Mapping(target = "deleted", constant = "false")
  Driver toEntity(DriverRequest request);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "brand", source = "carBrand")
  @Mapping(target = "color", source = "carColor")
  @Mapping(target = "licensePlate", source = "carLicensePlate")
  @Mapping(target = "driver", ignore = true)
  Car toCar(DriverRequest request);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "car", ignore = true)
  @Mapping(target = "email", ignore = true)
  @Mapping(target = "deleted", ignore = true)
  void updateDriverFromRequest(DriverRequest request, @MappingTarget Driver driver);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "brand", source = "carBrand")
  @Mapping(target = "color", source = "carColor")
  @Mapping(target = "licensePlate", source = "carLicensePlate")
  @Mapping(target = "driver", ignore = true)
  void updateCarFromRequest(DriverRequest request, @MappingTarget Car car);
}