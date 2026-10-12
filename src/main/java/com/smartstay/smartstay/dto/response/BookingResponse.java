package com.smartstay.smartstay.dto.response;

import com.smartstay.smartstay.entity.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private Long id;
    private Long guestId;
    private String guestName;
    private String guestEmail;
    private Long roomId;
    private String roomNumber;
    private String roomType;
    private Long hotelId;
    private String hotelName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfGuests;
    private BookingStatus status;
    private BigDecimal totalPrice;
    private String specialRequests;
    private LocalDateTime createdAt;
}
