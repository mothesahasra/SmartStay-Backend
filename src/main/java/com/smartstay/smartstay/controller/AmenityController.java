package com.smartstay.smartstay.controller;

import com.smartstay.smartstay.dto.request.AmenityRequest;
import com.smartstay.smartstay.dto.response.AmenityResponse;
import com.smartstay.smartstay.dto.response.ApiResponse;
import com.smartstay.smartstay.service.AmenityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/amenities")
@RequiredArgsConstructor
public class AmenityController {

    private final AmenityService amenityService;

    // --- Public Endpoints ---

    @GetMapping
    public ResponseEntity<ApiResponse<List<AmenityResponse>>> getAllAmenities() {
        List<AmenityResponse> amenities = amenityService.getAllAmenities();
        return ResponseEntity.ok(ApiResponse.success(amenities));
    }

    @GetMapping("/{amenityId}")
    public ResponseEntity<ApiResponse<AmenityResponse>> getAmenityById(@PathVariable Long amenityId) {
        AmenityResponse amenity = amenityService.getAmenityById(amenityId);
        return ResponseEntity.ok(ApiResponse.success(amenity));
    }

    // --- Admin Endpoints ---

    @PostMapping
    public ResponseEntity<ApiResponse<AmenityResponse>> createAmenity(
            @Valid @RequestBody AmenityRequest request) {

        AmenityResponse amenity = amenityService.createAmenity(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Amenity created successfully", amenity));
    }

    @PutMapping("/{amenityId}")
    public ResponseEntity<ApiResponse<AmenityResponse>> updateAmenity(
            @PathVariable Long amenityId,
            @Valid @RequestBody AmenityRequest request) {

        AmenityResponse amenity = amenityService.updateAmenity(amenityId, request);
        return ResponseEntity.ok(ApiResponse.success("Amenity updated successfully", amenity));
    }

    @DeleteMapping("/{amenityId}")
    public ResponseEntity<ApiResponse<Void>> deleteAmenity(@PathVariable Long amenityId) {
        amenityService.deleteAmenity(amenityId);
        return ResponseEntity.ok(ApiResponse.success("Amenity deleted successfully"));
    }
}
