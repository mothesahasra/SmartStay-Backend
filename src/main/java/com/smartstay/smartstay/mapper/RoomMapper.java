package com.smartstay.smartstay.mapper;

import com.smartstay.smartstay.dto.request.RoomRequest;
import com.smartstay.smartstay.dto.response.RoomResponse;
import com.smartstay.smartstay.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", builder = @org.mapstruct.Builder(disableBuilder = true), uses = {AmenityMapper.class})
public interface RoomMapper {

    @Mapping(target = "hotelId", source = "hotel.id")
    @Mapping(target = "hotelName", source = "hotel.name")
    RoomResponse toResponse(Room room);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "imageUrls", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Room toEntity(RoomRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "imageUrls", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(RoomRequest request, @MappingTarget Room room);
}
