package com.smartstay.smartstay.service.impl;

import com.smartstay.smartstay.dto.request.BookingRequest;
import com.smartstay.smartstay.dto.response.BookingResponse;
import com.smartstay.smartstay.dto.response.PagedResponse;
import com.smartstay.smartstay.entity.Booking;
import com.smartstay.smartstay.entity.BookingStatus;
import com.smartstay.smartstay.entity.Room;
import com.smartstay.smartstay.entity.User;
import com.smartstay.smartstay.exception.BadRequestException;
import com.smartstay.smartstay.exception.ResourceNotFoundException;
import com.smartstay.smartstay.exception.RoomNotAvailableException;
import com.smartstay.smartstay.exception.UnauthorizedException;
import com.smartstay.smartstay.mapper.BookingMapper;
import com.smartstay.smartstay.repository.BookingRepository;
import com.smartstay.smartstay.repository.RoomRepository;
import com.smartstay.smartstay.repository.UserRepository;
import com.smartstay.smartstay.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request, Long guestId) {
        User guest = userRepository.findById(guestId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", guestId));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", request.getRoomId()));

        if (!room.getIsAvailable()) {
            throw new RoomNotAvailableException(room.getId());
        }

        validateDates(request.getCheckInDate(), request.getCheckOutDate());

        if (request.getNumberOfGuests() > room.getCapacity()) {
            throw new BadRequestException("Number of guests exceeds room capacity of " + room.getCapacity());
        }

        // Check for overlapping bookings
        List<Booking> overlapping = bookingRepository.findOverlappingBookings(
                room.getId(), request.getCheckInDate(), request.getCheckOutDate());
        if (!overlapping.isEmpty()) {
            throw new RoomNotAvailableException(room.getId());
        }

        // Calculate total price
        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        BigDecimal totalPrice = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        Booking booking = Booking.builder()
                .guest(guest)
                .room(room)
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .numberOfGuests(request.getNumberOfGuests())
                .totalPrice(totalPrice)
                .specialRequests(request.getSpecialRequests())
                .status(BookingStatus.PENDING)
                .build();

        booking = bookingRepository.save(booking);
        return bookingMapper.toResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long bookingId, Long userId) {
        Booking booking = findBookingOrThrow(bookingId);

        // Allow guest, hotel manager, or admin to view
        boolean isGuest = booking.getGuest().getId().equals(userId);
        boolean isManager = booking.getRoom().getHotel().getManager().getId().equals(userId);

        if (!isGuest && !isManager) {
            throw new UnauthorizedException("You do not have permission to view this booking");
        }

        return bookingMapper.toResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<BookingResponse> getBookingsByGuest(Long guestId, Pageable pageable) {
        Page<Booking> page = bookingRepository.findByGuestId(guestId, pageable);
        return toPagedResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<BookingResponse> getBookingsByHotel(Long hotelId, Pageable pageable) {
        Page<Booking> page = bookingRepository.findByRoomHotelId(hotelId, pageable);
        return toPagedResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<BookingResponse> getBookingsByManager(Long managerId, Pageable pageable) {
        Page<Booking> page = bookingRepository.findByRoomHotelManagerId(managerId, pageable);
        return toPagedResponse(page);
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(Long bookingId, Long userId) {
        Booking booking = findBookingOrThrow(bookingId);

        boolean isGuest = booking.getGuest().getId().equals(userId);
        boolean isManager = booking.getRoom().getHotel().getManager().getId().equals(userId);

        if (!isGuest && !isManager) {
            throw new UnauthorizedException("You do not have permission to cancel this booking");
        }

        if (booking.getStatus() == BookingStatus.CHECKED_IN || booking.getStatus() == BookingStatus.CHECKED_OUT) {
            throw new BadRequestException("Cannot cancel a booking that is already checked in or checked out");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking = bookingRepository.save(booking);
        return bookingMapper.toResponse(booking);
    }

    @Override
    @Transactional
    public BookingResponse confirmBooking(Long bookingId, Long managerId) {
        Booking booking = findBookingOrThrow(bookingId);
        verifyManagerAccess(booking, managerId);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BadRequestException("Only pending bookings can be confirmed");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        booking = bookingRepository.save(booking);
        return bookingMapper.toResponse(booking);
    }

    @Override
    @Transactional
    public BookingResponse checkIn(Long bookingId, Long managerId) {
        Booking booking = findBookingOrThrow(bookingId);
        verifyManagerAccess(booking, managerId);

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BadRequestException("Only confirmed bookings can be checked in");
        }

        booking.setStatus(BookingStatus.CHECKED_IN);
        booking = bookingRepository.save(booking);
        return bookingMapper.toResponse(booking);
    }

    @Override
    @Transactional
    public BookingResponse checkOut(Long bookingId, Long managerId) {
        Booking booking = findBookingOrThrow(bookingId);
        verifyManagerAccess(booking, managerId);

        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new BadRequestException("Only checked-in bookings can be checked out");
        }

        booking.setStatus(BookingStatus.CHECKED_OUT);
        booking = bookingRepository.save(booking);
        return bookingMapper.toResponse(booking);
    }

    // --- Helpers ---

    private Booking findBookingOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));
    }

    private void verifyManagerAccess(Booking booking, Long managerId) {
        if (!booking.getRoom().getHotel().getManager().getId().equals(managerId)) {
            throw new UnauthorizedException("You are not the manager of this hotel");
        }
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (!checkOut.isAfter(checkIn)) {
            throw new BadRequestException("Check-out date must be after check-in date");
        }
    }

    private PagedResponse<BookingResponse> toPagedResponse(Page<Booking> page) {
        List<BookingResponse> content = page.getContent().stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
        return PagedResponse.<BookingResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
