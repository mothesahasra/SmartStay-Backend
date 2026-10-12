package com.smartstay.smartstay.repository;

import com.smartstay.smartstay.entity.Payment;
import com.smartstay.smartstay.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByBookingId(Long bookingId);

    Optional<Payment> findByTransactionReference(String transactionReference);

    List<Payment> findByTransactionStatus(PaymentStatus status);

    List<Payment> findByBookingGuestId(Long guestId);

    boolean existsByBookingId(Long bookingId);
}
