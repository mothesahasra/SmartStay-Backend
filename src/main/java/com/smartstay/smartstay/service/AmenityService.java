package com.smartstay.smartstay.service;

import com.smartstay.smartstay.dto.request.AmenityRequest;
import com.smartstay.smartstay.dto.response.AmenityResponse;

import java.util.List;

public interface AmenityService {

    AmenityResponse createAmenity(AmenityRequest request);

    AmenityResponse updateAmenity(Long amenityId, AmenityRequest request);

    void deleteAmenity(Long amenityId);

    AmenityResponse getAmenityById(Long amenityId);

    List<AmenityResponse> getAllAmenities();
}
