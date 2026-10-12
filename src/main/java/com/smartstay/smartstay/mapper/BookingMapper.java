package com.smartstay.smartstay.mapper;

import com.smartstay.smartstay.dto.response.BookingResponse;
import com.smartstay.smartstay.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "guestId", source = "guest.id")
    @Mapping(target = "guestName", source = "guest.name")
    @Mapping(target = "guestEmail", source = "guest.email")
    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "roomNumber", source = "room.roomNumber")
    @Mapping(target = "roomType", expression = "java(booking.getRoom().getRoomType().name())")
    @Mapping(target = "hotelId", source = "room.hotel.id")
    @Mapping(target = "hotelName", source = "room.hotel.name")
    BookingResponse toResponse(Booking booking);
}
