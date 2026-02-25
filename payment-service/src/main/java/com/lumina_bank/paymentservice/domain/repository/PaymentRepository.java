package com.lumina_bank.paymentservice.domain.repository;

import com.lumina_bank.paymentservice.domain.enums.PaymentStatus;
import com.lumina_bank.paymentservice.domain.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {
    List<Payment> findAllByPaymentStatusAndCreatedAtBefore(PaymentStatus paymentStatus, LocalDateTime minus);

    @Query("""
    SELECT p FROM Payment p
    WHERE
        (p.userId = :userId OR p.toAccountOwnerId = :userId)
        AND
        (:accountId IS NULL
            OR p.fromAccountId = :accountId
            OR p.toAccountId = :accountId)
    ORDER BY p.createdAt DESC
""")
    Page<Payment> findUserHistory(
            @Param("userId") Long userId,
            @Param("accountId") Long accountId,
            Pageable pageable
    );

    @Query("""
    SELECT p FROM Payment p
    WHERE
        (p.userId = :userId OR p.toAccountOwnerId = :userId)
        AND
        (:accountId IS NULL
            OR p.fromAccountId = :accountId
            OR p.toAccountId = :accountId)
    ORDER BY p.createdAt DESC
""")
    List<Payment> findUserHistory(
            @Param("userId") Long userId,
            @Param("accountId") Long accountId
    );
}