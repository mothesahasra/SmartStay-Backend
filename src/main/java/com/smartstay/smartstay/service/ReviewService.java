package com.smartstay.smartstay.service;

import com.smartstay.smartstay.dto.request.ReviewRequest;
import com.smartstay.smartstay.dto.response.PagedResponse;
import com.smartstay.smartstay.dto.response.ReviewResponse;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

    ReviewResponse createReview(ReviewRequest request, Long guestId);

    ReviewResponse updateReview(Long reviewId, ReviewRequest request, Long guestId);

    void deleteReview(Long reviewId, Long userId);

    PagedResponse<ReviewResponse> getReviewsByHotel(Long hotelId, Pageable pageable);

    PagedResponse<ReviewResponse> getReviewsByGuest(Long guestId, Pageable pageable);
}
