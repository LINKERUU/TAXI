package com.tripservice.mapper;

import com.tripservice.dto.TripRequest;
import com.tripservice.dto.TripResponse;
import com.tripservice.model.Address;
import com.tripservice.model.Trip;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TripMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", constant = "CREATED")
  @Mapping(target = "orderDateTime", ignore = true)
  @Mapping(target = "pickupAddress", expression = "java(mapToAddress(request.getPickupAddress()))")
  @Mapping(target = "destinationAddress", expression = "java(mapToAddress(request.getDestinationAddress()))")
  Trip toEntity(TripRequest request);

  @Mapping(target = "pickupAddress", expression = "java(mapAddressToString(trip.getPickupAddress()))")
  @Mapping(target = "destinationAddress", expression = "java(mapAddressToString(trip.getDestinationAddress()))")
  @Mapping(target = "orderTime", source = "orderDateTime")
  TripResponse toResponse(Trip trip);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "orderDateTime", ignore = true)
  @Mapping(target = "pickupAddress", ignore = true)
  @Mapping(target = "destinationAddress", ignore = true)
  void updateEntityFromRequest(TripRequest request, @MappingTarget Trip trip);

  @Named("toFallbackResponse")
  TripResponse toFallbackResponse(Trip trip);



  default Address mapToAddress(String addressString) {
    if (addressString == null || addressString.isBlank()) {
      return null;
    }

    String[] parts = addressString.split(", ");
    if (parts.length >= 3) {
      return new Address(parts[0], parts[1], parts[2]);
    }
    return new Address(addressString, "", "");
  }

  default String mapAddressToString(Address address) {
    if (address == null) {
      return "";
    }
    return address.getCity() + ", " + address.getStreet() + ", " + address.getBuildingNumber();
  }
}