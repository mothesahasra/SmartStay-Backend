package com.smartstay.smartstay.controller;

import com.smartstay.smartstay.dto.request.HotelRequest;
import com.smartstay.smartstay.dto.response.ApiResponse;
import com.smartstay.smartstay.dto.response.HotelResponse;
import com.smartstay.smartstay.dto.response.PagedResponse;
import com.smartstay.smartstay.entity.User;
import com.smartstay.smartstay.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    // --- Public Endpoints ---

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<HotelResponse>>> getAllHotels(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponse<HotelResponse> hotels = hotelService.getAllHotels(pageable);
        return ResponseEntity.ok(ApiResponse.success(hotels));
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<ApiResponse<HotelResponse>> getHotelById(@PathVariable Long hotelId) {
        HotelResponse hotel = hotelService.getHotelById(hotelId);
        return ResponseEntity.ok(ApiResponse.success(hotel));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PagedResponse<HotelResponse>>> searchHotels(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Integer starRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        if (keyword != null && !keyword.isBlank()) {
            return ResponseEntity.ok(ApiResponse.success(hotelService.searchByKeyword(keyword, pageable)));
        }
        PagedResponse<HotelResponse> hotels = hotelService.searchHotels(city, state, country, minRating, starRating, pageable);
        return ResponseEntity.ok(ApiResponse.success(hotels));
    }

    @GetMapping("/search/keyword")
    public ResponseEntity<ApiResponse<PagedResponse<HotelResponse>>> searchByKeyword(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<HotelResponse> hotels = hotelService.searchByKeyword(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(hotels));
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<ApiResponse<PagedResponse<HotelResponse>>> getHotelsByCity(
            @PathVariable String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<HotelResponse> hotels = hotelService.searchHotels(city, null, null, null, null, pageable);
        return ResponseEntity.ok(ApiResponse.success(hotels));
    }

    // --- Manager Endpoints ---

    @PostMapping
    public ResponseEntity<ApiResponse<HotelResponse>> createHotel(
            @Valid @RequestBody HotelRequest request,
            @AuthenticationPrincipal User currentUser) {

        HotelResponse hotel = hotelService.createHotel(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Hotel created successfully", hotel));
    }

    @PutMapping("/{hotelId}")
    public ResponseEntity<ApiResponse<HotelResponse>> updateHotel(
            @PathVariable Long hotelId,
            @Valid @RequestBody HotelRequest request,
            @AuthenticationPrincipal User currentUser) {

        HotelResponse hotel = hotelService.updateHotel(hotelId, request, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Hotel updated successfully", hotel));
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<ApiResponse<Void>> deleteHotel(
            @PathVariable Long hotelId,
            @AuthenticationPrincipal User currentUser) {

        hotelService.deleteHotel(hotelId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Hotel deleted successfully"));
    }

    @GetMapping("/my-hotels")
    public ResponseEntity<ApiResponse<List<HotelResponse>>> getMyHotels(
            @AuthenticationPrincipal User currentUser) {

        List<HotelResponse> hotels;
        if (currentUser.getRole() == com.smartstay.smartstay.entity.UserRole.ADMIN) {
            hotels = hotelService.getAllHotels(Pageable.unpaged()).getContent();
        } else {
            hotels = hotelService.getHotelsByManager(currentUser.getId());
        }
        return ResponseEntity.ok(ApiResponse.success(hotels));
    }
}
