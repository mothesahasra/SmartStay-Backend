package com.smartstay.smartstay.service.impl;

import com.smartstay.smartstay.dto.request.PaymentRequest;
import com.smartstay.smartstay.dto.response.PaymentResponse;
import com.smartstay.smartstay.entity.Booking;
import com.smartstay.smartstay.entity.BookingStatus;
import com.smartstay.smartstay.entity.Payment;
import com.smartstay.smartstay.entity.PaymentStatus;
import com.smartstay.smartstay.exception.BadRequestException;
import com.smartstay.smartstay.exception.ResourceNotFoundException;
import com.smartstay.smartstay.exception.UnauthorizedException;
import com.smartstay.smartstay.mapper.PaymentMapper;
import com.smartstay.smartstay.repository.BookingRepository;
import com.smartstay.smartstay.repository.PaymentRepository;
import com.smartstay.smartstay.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public PaymentResponse processPayment(PaymentRequest request, Long userId) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", request.getBookingId()));

        // Verify the booking belongs to this user
        if (!booking.getGuest().getId().equals(userId)) {
            throw new UnauthorizedException("You can only pay for your own bookings");
        }

        // Check if payment already exists for this booking
        if (paymentRepository.existsByBookingId(booking.getId())) {
            throw new BadRequestException("Payment already exists for this booking");
        }

        // Only pending or confirmed bookings can be paid
        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BadRequestException("Payment can only be made for pending or confirmed bookings");
        }

        // Create payment (mock processing — always succeeds)
        Payment payment = Payment.builder()
                .booking(booking)
                .amount(booking.getTotalPrice())
                .paymentMethod(request.getPaymentMethod())
                .transactionStatus(PaymentStatus.SUCCESS)
                .transactionReference("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase())
                .createdAt(LocalDateTime.now())
                .completedAt(LocalDateTime.now())
                .build();

        payment = paymentRepository.save(payment);

        // Auto-confirm booking on successful payment
        if (booking.getStatus() == BookingStatus.PENDING) {
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
        }

        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByBookingId(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));

        // Allow the guest who booked or the hotel manager to view
        boolean isGuest = booking.getGuest().getId().equals(userId);
        boolean isManager = booking.getRoom().getHotel().getManager().getId().equals(userId);
        if (!isGuest && !isManager) {
            throw new UnauthorizedException("You are not authorized to view this payment");
        }

        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "bookingId", bookingId));

        return paymentMapper.toResponse(payment);
    }

    @Override
    public PaymentResponse refundPayment(Long paymentId, Long managerId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));

        // Verify the manager owns the hotel
        if (!payment.getBooking().getRoom().getHotel().getManager().getId().equals(managerId)) {
            throw new UnauthorizedException("You can only refund payments for your own hotels");
        }

        if (payment.getTransactionStatus() != PaymentStatus.SUCCESS) {
            throw new BadRequestException("Only completed payments can be refunded");
        }

        payment.setTransactionStatus(PaymentStatus.REFUNDED);
        payment.setCompletedAt(LocalDateTime.now());
        payment = paymentRepository.save(payment);

        // Cancel the booking on refund
        Booking booking = payment.getBooking();
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        return paymentMapper.toResponse(payment);
    }
}
