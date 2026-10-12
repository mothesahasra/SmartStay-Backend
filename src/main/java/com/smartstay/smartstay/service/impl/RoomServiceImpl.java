package com.smartstay.smartstay.service.impl;

import com.smartstay.smartstay.dto.request.RoomRequest;
import com.smartstay.smartstay.dto.response.PagedResponse;
import com.smartstay.smartstay.dto.response.RoomResponse;
import com.smartstay.smartstay.entity.Amenity;
import com.smartstay.smartstay.entity.Hotel;
import com.smartstay.smartstay.entity.Room;
import com.smartstay.smartstay.entity.User;
import com.smartstay.smartstay.entity.UserRole;
import com.smartstay.smartstay.exception.BadRequestException;
import com.smartstay.smartstay.exception.ResourceNotFoundException;
import com.smartstay.smartstay.exception.UnauthorizedException;
import com.smartstay.smartstay.mapper.RoomMapper;
import com.smartstay.smartstay.repository.AmenityRepository;
import com.smartstay.smartstay.repository.HotelRepository;
import com.smartstay.smartstay.repository.RoomRepository;
import com.smartstay.smartstay.repository.UserRepository;
import com.smartstay.smartstay.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;
    private final AmenityRepository amenityRepository;
    private final RoomMapper roomMapper;

    @Override
    @Transactional
    public RoomResponse createRoom(RoomRequest request, Long managerId) {
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "id", request.getHotelId()));

        verifyManagerOwnership(hotel, managerId);

        Room room = roomMapper.toEntity(request);
        room.setHotel(hotel);

        if (room.getIsAvailable() == null) {
            room.setIsAvailable(true);
        }

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            room.setImageUrls(new ArrayList<>(request.getImageUrls()));
        } else if (request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
            room.setImageUrls(new ArrayList<>(List.of(request.getImageUrl())));
        }

        if (request.getAmenityIds() != null && !request.getAmenityIds().isEmpty()) {
            Set<Amenity> amenities = new HashSet<>(amenityRepository.findAllById(request.getAmenityIds()));
            room.setAmenities(amenities);
        }

        room = roomRepository.save(room);
        return roomMapper.toResponse(room);
    }

    @Override
    @Transactional
    public RoomResponse updateRoom(Long roomId, RoomRequest request, Long managerId) {
        Room room = findRoomOrThrow(roomId);
        verifyManagerOwnership(room.getHotel(), managerId);

        roomMapper.updateEntity(request, room);

        if (request.getIsAvailable() != null) {
            room.setIsAvailable(request.getIsAvailable());
        }

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            room.setImageUrls(new ArrayList<>(request.getImageUrls()));
        } else if (request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
            room.setImageUrls(new ArrayList<>(List.of(request.getImageUrl())));
        }

        if (request.getAmenityIds() != null) {
            Set<Amenity> amenities = new HashSet<>(amenityRepository.findAllById(request.getAmenityIds()));
            room.setAmenities(amenities);
        }

        room = roomRepository.save(room);
        return roomMapper.toResponse(room);
    }

    @Override
    @Transactional
    public void deleteRoom(Long roomId, Long managerId) {
        Room room = findRoomOrThrow(roomId);
        verifyManagerOwnership(room.getHotel(), managerId);
        roomRepository.delete(room);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponse getRoomById(Long roomId) {
        Room room = findRoomOrThrow(roomId);
        return roomMapper.toResponse(room);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<RoomResponse> getRoomsByHotel(Long hotelId, Pageable pageable) {
        Page<Room> page = roomRepository.findByHotelId(hotelId, pageable);
        List<RoomResponse> content = page.getContent().stream()
                .map(roomMapper::toResponse)
                .collect(Collectors.toList());
        return PagedResponse.<RoomResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> getAvailableRooms(Long hotelId, LocalDate checkIn, LocalDate checkOut) {
        validateDates(checkIn, checkOut);
        return roomRepository.findAvailableRooms(hotelId, checkIn, checkOut).stream()
                .map(roomMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> getAvailableRoomsByCapacity(Long hotelId, LocalDate checkIn, LocalDate checkOut, Integer minCapacity) {
        validateDates(checkIn, checkOut);
        return roomRepository.findAvailableRoomsByCapacity(hotelId, checkIn, checkOut, minCapacity).stream()
                .map(roomMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkRoomAvailability(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        validateDates(checkIn, checkOut);
        findRoomOrThrow(roomId);
        return roomRepository.isRoomAvailable(roomId, checkIn, checkOut);
    }

    // --- Helpers ---

    private Room findRoomOrThrow(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId));
    }

    private void verifyManagerOwnership(Hotel hotel, Long managerId) {
        User user = userRepository.findById(managerId).orElse(null);
        if (user != null && user.getRole() == UserRole.ADMIN) {
            return;
        }
        if (!hotel.getManager().getId().equals(managerId)) {
            throw new UnauthorizedException("You are not the manager of this hotel");
        }
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new BadRequestException("Check-in and check-out dates are required");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new BadRequestException("Check-out date must be after check-in date");
        }
    }
}
