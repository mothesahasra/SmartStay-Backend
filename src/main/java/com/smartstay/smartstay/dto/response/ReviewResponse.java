package com.smartstay.smartstay.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {
    private Long id;
    private Long guestId;
    private String guestName;
    private Long hotelId;
    private String hotelName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
