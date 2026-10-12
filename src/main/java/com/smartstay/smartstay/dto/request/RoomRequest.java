package com.smartstay.smartstay.dto.request;

import com.smartstay.smartstay.entity.RoomType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomRequest {

    @NotNull(message = "Hotel ID is required")
    private Long hotelId;

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @NotNull(message = "Room type is required")
    private RoomType roomType;

    private String description;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @NotNull(message = "Price per night is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal pricePerNight;

    private Boolean isAvailable;

    private List<Long> amenityIds;

    private String imageUrl;
    private List<String> imageUrls;

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        if ((this.imageUrls == null || this.imageUrls.isEmpty()) && imageUrl != null && !imageUrl.isBlank()) {
            this.imageUrls = new java.util.ArrayList<>(java.util.List.of(imageUrl));
        }
    }
}
