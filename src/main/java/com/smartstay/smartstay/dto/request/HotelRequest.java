package com.smartstay.smartstay.dto.request;

import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelRequest {

    @NotBlank(message = "Hotel name is required")
    @Size(min = 2, max = 200, message = "Hotel name must be between 2 and 200 characters")
    private String name;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    @Min(value = 1, message = "Star rating must be at least 1")
    @Max(value = 5, message = "Star rating must be at most 5")
    private Integer starRating;

    private String contactEmail;
    private String contactPhone;

    // Address fields
    private String street;

    private String city;

    private String state;

    private String country;

    private String zipCode;
    private Double latitude;
    private Double longitude;

    private List<Long> amenityIds;

    private List<String> imageUrls;
    private String imageUrl;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressDto {
        private String street;
        private String city;
        private String state;
        private String country;
        private String zipCode;
        private String postalCode;
        private Double latitude;
        private Double longitude;
    }

    private AddressDto address;

    @NotBlank(message = "City is required")
    public String getCity() {
        if (city != null && !city.isBlank()) return city;
        if (address != null && address.getCity() != null) return address.getCity();
        return city;
    }

    @NotBlank(message = "Country is required")
    public String getCountry() {
        if (country != null && !country.isBlank()) return country;
        if (address != null && address.getCountry() != null) return address.getCountry();
        return country;
    }

    public String getStreet() {
        if (street != null && !street.isBlank()) return street;
        if (address != null && address.getStreet() != null) return address.getStreet();
        return street;
    }

    public String getState() {
        if (state != null && !state.isBlank()) return state;
        if (address != null && address.getState() != null) return address.getState();
        return state;
    }

    public String getZipCode() {
        if (zipCode != null && !zipCode.isBlank()) return zipCode;
        if (address != null) {
            return address.getZipCode() != null ? address.getZipCode() : address.getPostalCode();
        }
        return zipCode;
    }

    @JsonSetter("address")
    public void setAddress(AddressDto address) {
        this.address = address;
        if (address != null) {
            if ((this.city == null || this.city.isBlank()) && address.getCity() != null) {
                this.city = address.getCity();
            }
            if ((this.country == null || this.country.isBlank()) && address.getCountry() != null) {
                this.country = address.getCountry();
            }
            if ((this.street == null || this.street.isBlank()) && address.getStreet() != null) {
                this.street = address.getStreet();
            }
            if ((this.state == null || this.state.isBlank()) && address.getState() != null) {
                this.state = address.getState();
            }
            if (this.zipCode == null || this.zipCode.isBlank()) {
                this.zipCode = address.getZipCode() != null ? address.getZipCode() : address.getPostalCode();
            }
            if (this.latitude == null && address.getLatitude() != null) {
                this.latitude = address.getLatitude();
            }
            if (this.longitude == null && address.getLongitude() != null) {
                this.longitude = address.getLongitude();
            }
        }
    }

    @JsonSetter("phone")
    public void setPhone(String phone) {
        if (this.contactPhone == null || this.contactPhone.isBlank()) {
            this.contactPhone = phone;
        }
    }

    @JsonSetter("email")
    public void setEmail(String email) {
        if (this.contactEmail == null || this.contactEmail.isBlank()) {
            this.contactEmail = email;
        }
    }

    @JsonSetter("postalCode")
    public void setPostalCode(String postalCode) {
        if (this.zipCode == null || this.zipCode.isBlank()) {
            this.zipCode = postalCode;
        }
    }

    @JsonSetter("imageUrl")
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        if ((this.imageUrls == null || this.imageUrls.isEmpty()) && imageUrl != null && !imageUrl.isBlank()) {
            this.imageUrls = new ArrayList<>(List.of(imageUrl));
        }
    }
}
