package com.smartstay.smartstay.service;

import com.smartstay.smartstay.dto.request.PaymentRequest;
import com.smartstay.smartstay.dto.response.PaymentResponse;

public interface PaymentService {

    PaymentResponse processPayment(PaymentRequest request, Long userId);

    PaymentResponse getPaymentByBookingId(Long bookingId, Long userId);

    PaymentResponse refundPayment(Long paymentId, Long managerId);
}
