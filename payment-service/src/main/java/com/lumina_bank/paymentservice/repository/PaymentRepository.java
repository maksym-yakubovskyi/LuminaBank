package com.lumina_bank.paymentservice.repository;

import com.lumina_bank.paymentservice.enums.PaymentStatus;
import com.lumina_bank.paymentservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {
    List<Payment> findAllByPaymentStatusAndCreatedAtBefore(PaymentStatus paymentStatus, LocalDateTime minus);

    Page<Payment> findByFromAccountIdOrToAccountId(Long fromId, Long toId, Pageable pageable);

    List<Payment> findByFromAccountIdOrToAccountId(Long accountId, Long accountId1);
}