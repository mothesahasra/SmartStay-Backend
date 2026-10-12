package com.smartstay.smartstay.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmenityResponse {
    private Long id;
    private String name;
    private String description;
    private String icon;
}
