package com.smartstay.smartstay.service.impl;

import com.smartstay.smartstay.dto.request.AmenityRequest;
import com.smartstay.smartstay.dto.response.AmenityResponse;
import com.smartstay.smartstay.entity.Amenity;
import com.smartstay.smartstay.exception.BadRequestException;
import com.smartstay.smartstay.exception.ResourceNotFoundException;
import com.smartstay.smartstay.mapper.AmenityMapper;
import com.smartstay.smartstay.repository.AmenityRepository;
import com.smartstay.smartstay.service.AmenityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AmenityServiceImpl implements AmenityService {

    private final AmenityRepository amenityRepository;
    private final AmenityMapper amenityMapper;

    @Override
    public AmenityResponse createAmenity(AmenityRequest request) {
        // Check for duplicate name
        if (amenityRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("Amenity with name '" + request.getName() + "' already exists");
        }

        Amenity amenity = amenityMapper.toEntity(request);
        amenity = amenityRepository.save(amenity);
        return amenityMapper.toResponse(amenity);
    }

    @Override
    public AmenityResponse updateAmenity(Long amenityId, AmenityRequest request) {
        Amenity amenity = amenityRepository.findById(amenityId)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity", "id", amenityId));

        // Check for duplicate name (excluding self)
        amenityRepository.findByNameIgnoreCase(request.getName())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(amenityId)) {
                        throw new BadRequestException("Amenity with name '" + request.getName() + "' already exists");
                    }
                });

        amenityMapper.updateEntity(request, amenity);
        amenity = amenityRepository.save(amenity);
        return amenityMapper.toResponse(amenity);
    }

    @Override
    public void deleteAmenity(Long amenityId) {
        Amenity amenity = amenityRepository.findById(amenityId)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity", "id", amenityId));
        amenityRepository.delete(amenity);
    }

    @Override
    @Transactional(readOnly = true)
    public AmenityResponse getAmenityById(Long amenityId) {
        Amenity amenity = amenityRepository.findById(amenityId)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity", "id", amenityId));
        return amenityMapper.toResponse(amenity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AmenityResponse> getAllAmenities() {
        return amenityRepository.findAll()
                .stream()
                .map(amenityMapper::toResponse)
                .collect(Collectors.toList());
    }
}
