package com.smartstay.smartstay.service;

import com.smartstay.smartstay.dto.request.RoomRequest;
import com.smartstay.smartstay.dto.response.PagedResponse;
import com.smartstay.smartstay.dto.response.RoomResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface RoomService {

    RoomResponse createRoom(RoomRequest request, Long managerId);

    RoomResponse updateRoom(Long roomId, RoomRequest request, Long managerId);

    void deleteRoom(Long roomId, Long managerId);

    RoomResponse getRoomById(Long roomId);

    PagedResponse<RoomResponse> getRoomsByHotel(Long hotelId, Pageable pageable);

    List<RoomResponse> getAvailableRooms(Long hotelId, LocalDate checkIn, LocalDate checkOut);

    List<RoomResponse> getAvailableRoomsByCapacity(Long hotelId, LocalDate checkIn, LocalDate checkOut, Integer minCapacity);

    boolean checkRoomAvailability(Long roomId, LocalDate checkIn, LocalDate checkOut);
}
