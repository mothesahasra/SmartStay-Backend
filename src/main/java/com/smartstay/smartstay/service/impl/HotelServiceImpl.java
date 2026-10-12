package com.smartstay.smartstay.service.impl;

import com.smartstay.smartstay.dto.request.HotelRequest;
import com.smartstay.smartstay.dto.response.HotelResponse;
import com.smartstay.smartstay.dto.response.PagedResponse;
import com.smartstay.smartstay.entity.Address;
import com.smartstay.smartstay.entity.Amenity;
import com.smartstay.smartstay.entity.Hotel;
import com.smartstay.smartstay.entity.User;
import com.smartstay.smartstay.entity.UserRole;
import com.smartstay.smartstay.exception.BadRequestException;
import com.smartstay.smartstay.exception.ResourceNotFoundException;
import com.smartstay.smartstay.mapper.HotelMapper;
import com.smartstay.smartstay.repository.AmenityRepository;
import com.smartstay.smartstay.repository.HotelRepository;
import com.smartstay.smartstay.repository.UserRepository;
import com.smartstay.smartstay.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;
    private final AmenityRepository amenityRepository;
    private final HotelMapper hotelMapper;

    @Override
    @Transactional
    public HotelResponse createHotel(HotelRequest request, Long managerId) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", managerId));

        Hotel hotel = hotelMapper.toEntity(request);
        hotel.setManager(manager);

        Address address = Address.builder()
                .street(request.getStreet())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .zipCode(request.getZipCode())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
        hotel.setAddress(address);

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            hotel.setImageUrls(new ArrayList<>(request.getImageUrls()));
        } else if (request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
            hotel.setImageUrls(new ArrayList<>(List.of(request.getImageUrl())));
        }

        if (request.getAmenityIds() != null && !request.getAmenityIds().isEmpty()) {
            Set<Amenity> amenities = new HashSet<>(amenityRepository.findAllById(request.getAmenityIds()));
            hotel.setAmenities(amenities);
        }

        hotel = hotelRepository.save(hotel);
        return hotelMapper.toResponse(hotel);
    }

    @Override
    @Transactional
    public HotelResponse updateHotel(Long hotelId, HotelRequest request, Long managerId) {
        Hotel hotel = findHotelOrThrow(hotelId);
        verifyManagerOwnership(hotel, managerId);

        hotelMapper.updateEntity(request, hotel);

        Address address = Address.builder()
                .street(request.getStreet())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .zipCode(request.getZipCode())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
        hotel.setAddress(address);

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            hotel.setImageUrls(new ArrayList<>(request.getImageUrls()));
        } else if (request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
            hotel.setImageUrls(new ArrayList<>(List.of(request.getImageUrl())));
        }

        if (request.getAmenityIds() != null) {
            Set<Amenity> amenities = new HashSet<>(amenityRepository.findAllById(request.getAmenityIds()));
            hotel.setAmenities(amenities);
        }

        hotel = hotelRepository.save(hotel);
        return hotelMapper.toResponse(hotel);
    }

    @Override
    @Transactional
    public void deleteHotel(Long hotelId, Long userId) {
        Hotel hotel = findHotelOrThrow(hotelId);
        // Admin can delete any hotel; manager can only delete their own
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        if (!user.getRole().name().equals("ADMIN")) {
            verifyManagerOwnership(hotel, userId);
        }
        hotelRepository.delete(hotel);
    }

    @Override
    public HotelResponse getHotelById(Long hotelId) {
        return hotelMapper.toResponse(findHotelOrThrow(hotelId));
    }

    @Override
    public PagedResponse<HotelResponse> getAllHotels(Pageable pageable) {
        return toPagedResponse(hotelRepository.findAll(pageable));
    }

    @Override
    public PagedResponse<HotelResponse> searchHotels(String city, String state, String country,
                                                      Double minRating, Integer starRating, Pageable pageable) {
        return toPagedResponse(hotelRepository.searchHotels(city, state, country, minRating, starRating, pageable));
    }

    @Override
    public PagedResponse<HotelResponse> searchByKeyword(String keyword, Pageable pageable) {
        return toPagedResponse(hotelRepository.searchByKeyword(keyword, pageable));
    }

    @Override
    public List<HotelResponse> getHotelsByManager(Long managerId) {
        return hotelRepository.findByManagerId(managerId).stream()
                .map(hotelMapper::toResponse).toList();
    }

    @Override
    public PagedResponse<HotelResponse> getHotelsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return toPagedResponse(hotelRepository.findByPriceRange(minPrice, maxPrice, pageable));
    }

    private Hotel findHotelOrThrow(Long hotelId) {
        return hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "id", hotelId));
    }

    private void verifyManagerOwnership(Hotel hotel, Long managerId) {
        User user = userRepository.findById(managerId).orElse(null);
        if (user != null && user.getRole() == UserRole.ADMIN) {
            return;
        }
        if (!hotel.getManager().getId().equals(managerId)) {
            throw new BadRequestException("You do not have permission to manage this hotel");
        }
    }

    private PagedResponse<HotelResponse> toPagedResponse(Page<Hotel> page) {
        return PagedResponse.<HotelResponse>builder()
                .content(page.getContent().stream().map(hotelMapper::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
