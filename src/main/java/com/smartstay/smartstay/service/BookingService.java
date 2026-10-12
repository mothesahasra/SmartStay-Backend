package com.smartstay.smartstay.service;

import com.smartstay.smartstay.dto.request.BookingRequest;
import com.smartstay.smartstay.dto.response.BookingResponse;
import com.smartstay.smartstay.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface BookingService {

    BookingResponse createBooking(BookingRequest request, Long guestId);

    BookingResponse getBookingById(Long bookingId, Long userId);

    PagedResponse<BookingResponse> getBookingsByGuest(Long guestId, Pageable pageable);

    PagedResponse<BookingResponse> getBookingsByHotel(Long hotelId, Pageable pageable);

    PagedResponse<BookingResponse> getBookingsByManager(Long managerId, Pageable pageable);

    BookingResponse cancelBooking(Long bookingId, Long userId);

    BookingResponse confirmBooking(Long bookingId, Long managerId);

    BookingResponse checkIn(Long bookingId, Long managerId);

    BookingResponse checkOut(Long bookingId, Long managerId);
}
