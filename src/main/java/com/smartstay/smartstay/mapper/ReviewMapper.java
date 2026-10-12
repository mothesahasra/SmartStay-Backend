package com.smartstay.smartstay.mapper;

import com.smartstay.smartstay.dto.response.ReviewResponse;
import com.smartstay.smartstay.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "guestId", source = "guest.id")
    @Mapping(target = "guestName", source = "guest.name")
    @Mapping(target = "hotelId", source = "hotel.id")
    @Mapping(target = "hotelName", source = "hotel.name")
    ReviewResponse toResponse(Review review);
}
