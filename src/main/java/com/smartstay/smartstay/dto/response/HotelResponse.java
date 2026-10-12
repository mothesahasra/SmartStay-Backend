package com.smartstay.smartstay.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelResponse {
    private Long id;
    private String name;
    private String description;
    private Integer starRating;
    private String contactEmail;
    private String contactPhone;
    private Long managerId;
    private String managerName;
    private AddressResponse address;
    private List<String> imageUrls;
    private Set<AmenityResponse> amenities;
    private Double averageRating;
    private Integer totalReviews;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getImageUrl() {
        return (imageUrls != null && !imageUrls.isEmpty()) ? imageUrls.get(0) : null;
    }

    public String getPhone() {
        return contactPhone;
    }

    public String getEmail() {
        return contactEmail;
    }
}
