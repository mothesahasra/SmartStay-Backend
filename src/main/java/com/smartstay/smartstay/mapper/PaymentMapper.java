package com.smartstay.smartstay.mapper;

import com.smartstay.smartstay.dto.response.PaymentResponse;
import com.smartstay.smartstay.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "bookingId", source = "booking.id")
    PaymentResponse toResponse(Payment payment);
}
