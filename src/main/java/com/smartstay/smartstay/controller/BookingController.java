package com.smartstay.smartstay.controller;

import com.smartstay.smartstay.dto.request.BookingRequest;
import com.smartstay.smartstay.dto.response.ApiResponse;
import com.smartstay.smartstay.dto.response.BookingResponse;
import com.smartstay.smartstay.dto.response.PagedResponse;
import com.smartstay.smartstay.entity.User;
import com.smartstay.smartstay.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // --- Guest Endpoints ---

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(
            @Valid @RequestBody BookingRequest request,
            @AuthenticationPrincipal User currentUser) {

        BookingResponse booking = bookingService.createBooking(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Booking created successfully", booking));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal User currentUser) {

        BookingResponse booking = bookingService.getBookingById(bookingId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(booking));
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<ApiResponse<PagedResponse<BookingResponse>>> getMyBookings(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PagedResponse<BookingResponse> bookings = bookingService.getBookingsByGuest(currentUser.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal User currentUser) {

        BookingResponse booking = bookingService.cancelBooking(bookingId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully", booking));
    }

    // --- Manager Endpoints ---

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<ApiResponse<PagedResponse<BookingResponse>>> getBookingsByHotel(
            @PathVariable Long hotelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PagedResponse<BookingResponse> bookings = bookingService.getBookingsByHotel(hotelId, pageable);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    @GetMapping("/manager")
    public ResponseEntity<ApiResponse<PagedResponse<BookingResponse>>> getManagerBookings(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PagedResponse<BookingResponse> bookings = bookingService.getBookingsByManager(currentUser.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    @PatchMapping("/{bookingId}/confirm")
    public ResponseEntity<ApiResponse<BookingResponse>> confirmBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal User currentUser) {

        BookingResponse booking = bookingService.confirmBooking(bookingId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Booking confirmed", booking));
    }

    @PatchMapping("/{bookingId}/check-in")
    public ResponseEntity<ApiResponse<BookingResponse>> checkIn(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal User currentUser) {

        BookingResponse booking = bookingService.checkIn(bookingId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Guest checked in", booking));
    }

    @PatchMapping("/{bookingId}/check-out")
    public ResponseEntity<ApiResponse<BookingResponse>> checkOut(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal User currentUser) {

        BookingResponse booking = bookingService.checkOut(bookingId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Guest checked out", booking));
    }
}
