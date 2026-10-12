package com.smartstay.smartstay.repository;

import com.smartstay.smartstay.entity.Room;
import com.smartstay.smartstay.entity.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByHotelId(Long hotelId);

    Page<Room> findByHotelId(Long hotelId, Pageable pageable);

    List<Room> findByHotelIdAndRoomType(Long hotelId, RoomType roomType);

    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId AND r.isAvailable = true AND " +
            "r.id NOT IN (" +
            "  SELECT b.room.id FROM Booking b WHERE b.room.hotel.id = :hotelId " +
            "  AND b.status NOT IN ('CANCELLED', 'CHECKED_OUT') " +
            "  AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate" +
            ")")
    List<Room> findAvailableRooms(
            @Param("hotelId") Long hotelId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );

    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId AND r.isAvailable = true AND " +
            "r.capacity >= :minCapacity AND " +
            "r.id NOT IN (" +
            "  SELECT b.room.id FROM Booking b WHERE b.room.hotel.id = :hotelId " +
            "  AND b.status NOT IN ('CANCELLED', 'CHECKED_OUT') " +
            "  AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate" +
            ")")
    List<Room> findAvailableRoomsByCapacity(
            @Param("hotelId") Long hotelId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("minCapacity") Integer minCapacity
    );

    @Query("SELECT CASE WHEN COUNT(b) = 0 THEN true ELSE false END FROM Booking b " +
            "WHERE b.room.id = :roomId " +
            "AND b.status NOT IN ('CANCELLED', 'CHECKED_OUT') " +
            "AND b.checkInDate < :checkOutDate AND b.checkOutDate > :checkInDate")
    boolean isRoomAvailable(
            @Param("roomId") Long roomId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );

    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId AND " +
            "r.pricePerNight BETWEEN :minPrice AND :maxPrice")
    List<Room> findByHotelIdAndPriceRange(
            @Param("hotelId") Long hotelId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );
}
