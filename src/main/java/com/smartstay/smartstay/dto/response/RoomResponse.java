package com.smartstay.smartstay.dto.response;

import com.smartstay.smartstay.entity.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomResponse {
    private Long id;
    private String roomNumber;
    private RoomType roomType;
    private String description;
    private Integer capacity;
    private BigDecimal pricePerNight;
    private Boolean isAvailable;
    private Long hotelId;
    private String hotelName;
    private List<String> imageUrls;
    private Set<AmenityResponse> amenities;

    public String getImageUrl() {
        return (imageUrls != null && !imageUrls.isEmpty()) ? imageUrls.get(0) : null;
    }
}
