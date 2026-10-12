package com.smartstay.smartstay.repository;

import com.smartstay.smartstay.entity.Booking;
import com.smartstay.smartstay.entity.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByGuestId(Long guestId, Pageable pageable);

    List<Booking> findByGuestIdAndStatus(Long guestId, BookingStatus status);

    Page<Booking> findByRoomHotelId(Long hotelId, Pageable pageable);

    Page<Booking> findByRoomHotelManagerId(Long managerId, Pageable pageable);

    List<Booking> findByRoomIdAndStatusNotIn(Long roomId, List<BookingStatus> excludedStatuses);

    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId " +
            "AND b.status NOT IN ('CANCELLED', 'CHECKED_OUT') " +
            "AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate")
    List<Booking> findOverlappingBookings(
            @Param("roomId") Long roomId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b " +
            "WHERE b.guest.id = :guestId AND b.room.hotel.id = :hotelId " +
            "AND b.status = 'CHECKED_OUT'")
    boolean hasCompletedStay(@Param("guestId") Long guestId, @Param("hotelId") Long hotelId);

    @Query("SELECT b FROM Booking b WHERE b.status = :status AND b.checkInDate <= :date")
    List<Booking> findByStatusAndCheckInDateBefore(
            @Param("status") BookingStatus status,
            @Param("date") LocalDate date
    );

    @Query("SELECT b FROM Booking b WHERE b.guest.id = :guestId " +
            "ORDER BY b.createdAt DESC")
    Page<Booking> findRecentBookingsByGuest(@Param("guestId") Long guestId, Pageable pageable);
}
