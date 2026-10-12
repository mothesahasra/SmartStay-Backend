package com.smartstay.smartstay.mapper;

import com.smartstay.smartstay.dto.request.AmenityRequest;
import com.smartstay.smartstay.dto.response.AmenityResponse;
import com.smartstay.smartstay.entity.Amenity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AmenityMapper {

    AmenityResponse toResponse(Amenity amenity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hotels", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    Amenity toEntity(AmenityRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hotels", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    void updateEntity(AmenityRequest request, @MappingTarget Amenity amenity);
}
