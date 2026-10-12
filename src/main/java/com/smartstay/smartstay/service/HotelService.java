package com.smartstay.smartstay.service;

import com.smartstay.smartstay.dto.request.HotelRequest;
import com.smartstay.smartstay.dto.response.HotelResponse;
import com.smartstay.smartstay.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface HotelService {

    HotelResponse createHotel(HotelRequest request, Long managerId);

    HotelResponse updateHotel(Long hotelId, HotelRequest request, Long managerId);

    void deleteHotel(Long hotelId, Long userId);

    HotelResponse getHotelById(Long hotelId);

    PagedResponse<HotelResponse> getAllHotels(Pageable pageable);

    PagedResponse<HotelResponse> searchHotels(String city, String state, String country,
                                               Double minRating, Integer starRating, Pageable pageable);

    PagedResponse<HotelResponse> searchByKeyword(String keyword, Pageable pageable);

    List<HotelResponse> getHotelsByManager(Long managerId);

    PagedResponse<HotelResponse> getHotelsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
}
