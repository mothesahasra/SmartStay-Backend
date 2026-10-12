package com.smartstay.smartstay.dto.response;

import com.smartstay.smartstay.entity.PaymentMethod;
import com.smartstay.smartstay.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private Long id;
    private Long bookingId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus transactionStatus;
    private String transactionReference;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
