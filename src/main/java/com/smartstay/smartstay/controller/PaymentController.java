package com.smartstay.smartstay.controller;

import com.smartstay.smartstay.dto.request.PaymentRequest;
import com.smartstay.smartstay.dto.response.ApiResponse;
import com.smartstay.smartstay.dto.response.PaymentResponse;
import com.smartstay.smartstay.entity.User;
import com.smartstay.smartstay.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal User currentUser) {

        PaymentResponse payment = paymentService.processPayment(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment processed successfully", payment));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByBookingId(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal User currentUser) {

        PaymentResponse payment = paymentService.getPaymentByBookingId(bookingId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(payment));
    }

    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<ApiResponse<PaymentResponse>> refundPayment(
            @PathVariable Long paymentId,
            @AuthenticationPrincipal User currentUser) {

        PaymentResponse payment = paymentService.refundPayment(paymentId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Payment refunded successfully", payment));
    }
}
