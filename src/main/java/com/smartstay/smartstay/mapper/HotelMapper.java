package com.smartstay.smartstay.mapper;

import com.smartstay.smartstay.dto.request.HotelRequest;
import com.smartstay.smartstay.dto.response.AddressResponse;
import com.smartstay.smartstay.dto.response.HotelResponse;
import com.smartstay.smartstay.entity.Address;
import com.smartstay.smartstay.entity.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = true), uses = {AmenityMapper.class})
public interface HotelMapper {

    @Mapping(target = "managerId", source = "manager.id")
    @Mapping(target = "managerName", source = "manager.name")
    HotelResponse toResponse(Hotel hotel);

    AddressResponse toAddressResponse(Address address);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "imageUrls", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "totalReviews", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Hotel toEntity(HotelRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "imageUrls", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "totalReviews", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(HotelRequest request, @MappingTarget Hotel hotel);
}
