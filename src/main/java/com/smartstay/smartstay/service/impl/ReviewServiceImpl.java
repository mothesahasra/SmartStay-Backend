package com.smartstay.smartstay.service.impl;

import com.smartstay.smartstay.dto.request.ReviewRequest;
import com.smartstay.smartstay.dto.response.PagedResponse;
import com.smartstay.smartstay.dto.response.ReviewResponse;
import com.smartstay.smartstay.entity.Hotel;
import com.smartstay.smartstay.entity.Review;
import com.smartstay.smartstay.entity.User;
import com.smartstay.smartstay.exception.BadRequestException;
import com.smartstay.smartstay.exception.ResourceNotFoundException;
import com.smartstay.smartstay.exception.UnauthorizedException;
import com.smartstay.smartstay.mapper.ReviewMapper;
import com.smartstay.smartstay.repository.BookingRepository;
import com.smartstay.smartstay.repository.HotelRepository;
import com.smartstay.smartstay.repository.ReviewRepository;
import com.smartstay.smartstay.repository.UserRepository;
import com.smartstay.smartstay.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public ReviewResponse createReview(ReviewRequest request, Long guestId) {
        User guest = userRepository.findById(guestId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", guestId));

        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "id", request.getHotelId()));

        // Check if the guest has already reviewed this hotel
        if (reviewRepository.existsByGuestIdAndHotelId(guestId, hotel.getId())) {
            throw new BadRequestException("You have already reviewed this hotel");
        }

        // Verify the guest has completed a stay at this hotel
        if (!bookingRepository.hasCompletedStay(guestId, hotel.getId())) {
            throw new BadRequestException("You can only review hotels where you have completed a stay");
        }

        Review review = Review.builder()
                .guest(guest)
                .hotel(hotel)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        review = reviewRepository.save(review);

        // Recalculate hotel average rating
        updateHotelRating(hotel);

        return reviewMapper.toResponse(review);
    }

    @Override
    public ReviewResponse updateReview(Long reviewId, ReviewRequest request, Long guestId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        if (!review.getGuest().getId().equals(guestId)) {
            throw new UnauthorizedException("You can only edit your own reviews");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review = reviewRepository.save(review);

        // Recalculate hotel average rating
        updateHotelRating(review.getHotel());

        return reviewMapper.toResponse(review);
    }

    @Override
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        // Allow the guest who wrote it or the hotel manager to delete
        boolean isGuest = review.getGuest().getId().equals(userId);
        boolean isManager = review.getHotel().getManager().getId().equals(userId);
        if (!isGuest && !isManager) {
            throw new UnauthorizedException("You are not authorized to delete this review");
        }

        Hotel hotel = review.getHotel();
        reviewRepository.delete(review);

        // Recalculate hotel average rating
        updateHotelRating(hotel);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReviewResponse> getReviewsByHotel(Long hotelId, Pageable pageable) {
        if (!hotelRepository.existsById(hotelId)) {
            throw new ResourceNotFoundException("Hotel", "id", hotelId);
        }

        Page<Review> page = reviewRepository.findByHotelId(hotelId, pageable);
        return toPagedResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReviewResponse> getReviewsByGuest(Long guestId, Pageable pageable) {
        List<Review> reviews = reviewRepository.findByGuestId(guestId);
        List<ReviewResponse> responseList = reviews.stream().map(reviewMapper::toResponse).toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), responseList.size());
        List<ReviewResponse> subList = start >= responseList.size()
                ? List.of()
                : responseList.subList(start, end);

        return PagedResponse.<ReviewResponse>builder()
                .content(subList)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalElements(responseList.size())
                .totalPages((int) Math.ceil((double) responseList.size() / pageable.getPageSize()))
                .last(end >= responseList.size())
                .build();
    }

    private void updateHotelRating(Hotel hotel) {
        Double avgRating = reviewRepository.calculateAverageRating(hotel.getId());
        Long totalReviews = reviewRepository.countByHotelId(hotel.getId());

        hotel.setAverageRating(avgRating != null ? avgRating : 0.0);
        hotel.setTotalReviews(totalReviews != null ? totalReviews.intValue() : 0);
        hotelRepository.save(hotel);
    }

    private PagedResponse<ReviewResponse> toPagedResponse(Page<Review> page) {
        return PagedResponse.<ReviewResponse>builder()
                .content(page.getContent().stream().map(reviewMapper::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
