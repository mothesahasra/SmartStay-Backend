package com.smartstay.smartstay.controller;

import com.smartstay.smartstay.dto.request.ReviewRequest;
import com.smartstay.smartstay.dto.response.ApiResponse;
import com.smartstay.smartstay.dto.response.PagedResponse;
import com.smartstay.smartstay.dto.response.ReviewResponse;
import com.smartstay.smartstay.entity.User;
import com.smartstay.smartstay.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // --- Public Endpoints ---

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewResponse>>> getReviewsByHotel(
            @PathVariable Long hotelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PagedResponse<ReviewResponse> reviews = reviewService.getReviewsByHotel(hotelId, pageable);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    // --- Guest Endpoints ---

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal User currentUser) {

        ReviewResponse review = reviewService.createReview(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review submitted successfully", review));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal User currentUser) {

        ReviewResponse review = reviewService.updateReview(reviewId, request, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Review updated successfully", review));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal User currentUser) {

        reviewService.deleteReview(reviewId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Review deleted successfully"));
    }

    @GetMapping("/my-reviews")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewResponse>>> getMyReviews(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PagedResponse<ReviewResponse> reviews = reviewService.getReviewsByGuest(currentUser.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }
}
