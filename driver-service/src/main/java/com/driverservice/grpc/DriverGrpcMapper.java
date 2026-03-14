package com.driverservice.grpc;

import com.driverservice.model.Driver;
import com.taxi.grpc.driver.DriverResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DriverGrpcMapper {

  DriverResponse toGrpc(Driver driver);

}