package com.smartstay.smartstay.controller;

import com.smartstay.smartstay.dto.request.RoomRequest;
import com.smartstay.smartstay.dto.response.ApiResponse;
import com.smartstay.smartstay.dto.response.PagedResponse;
import com.smartstay.smartstay.dto.response.RoomResponse;
import com.smartstay.smartstay.entity.User;
import com.smartstay.smartstay.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    // --- Public Endpoints ---

    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomById(@PathVariable Long roomId) {
        RoomResponse room = roomService.getRoomById(roomId);
        return ResponseEntity.ok(ApiResponse.success(room));
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<ApiResponse<PagedResponse<RoomResponse>>> getRoomsByHotel(
            @PathVariable Long hotelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<RoomResponse> rooms = roomService.getRoomsByHotel(hotelId, pageable);
        return ResponseEntity.ok(ApiResponse.success(rooms));
    }

    @GetMapping({"/hotel/{hotelId}/available", "/available"})
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAvailableRooms(
            @PathVariable(required = false) Long hotelId,
            @RequestParam(value = "hotelId", required = false) Long hotelIdParam,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate) {

        Long targetHotelId = hotelId != null ? hotelId : hotelIdParam;
        LocalDate start = checkIn != null ? checkIn : checkInDate;
        LocalDate end = checkOut != null ? checkOut : checkOutDate;

        List<RoomResponse> rooms = roomService.getAvailableRooms(targetHotelId, start, end);
        return ResponseEntity.ok(ApiResponse.success(rooms));
    }

    @GetMapping("/hotel/{hotelId}/available/filter")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAvailableRoomsByCapacity(
            @PathVariable Long hotelId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam Integer minCapacity) {

        List<RoomResponse> rooms = roomService.getAvailableRoomsByCapacity(hotelId, checkIn, checkOut, minCapacity);
        return ResponseEntity.ok(ApiResponse.success(rooms));
    }

    @GetMapping("/{roomId}/availability")
    public ResponseEntity<ApiResponse<Boolean>> checkRoomAvailability(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {

        boolean available = roomService.checkRoomAvailability(roomId, checkIn, checkOut);
        return ResponseEntity.ok(ApiResponse.success(available));
    }

    // --- Manager Endpoints ---

    @PostMapping
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(
            @Valid @RequestBody RoomRequest request,
            @AuthenticationPrincipal User currentUser) {

        RoomResponse room = roomService.createRoom(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Room created successfully", room));
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(
            @PathVariable Long roomId,
            @Valid @RequestBody RoomRequest request,
            @AuthenticationPrincipal User currentUser) {

        RoomResponse room = roomService.updateRoom(roomId, request, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Room updated successfully", room));
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal User currentUser) {

        roomService.deleteRoom(roomId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Room deleted successfully"));
    }
}
