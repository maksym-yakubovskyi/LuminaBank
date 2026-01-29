package com.lumina_bank.frauddetectionservice.repository;

import com.lumina_bank.frauddetectionservice.model.UserPaymentActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface UserPaymentActivityRepository extends JpaRepository<UserPaymentActivity,Long> {
    long countByUserIdAndOccurredAtAfter(
            Long userId,
            LocalDateTime after
    );

    @Modifying
    @Query("""
        DELETE FROM UserPaymentActivity a
        WHERE a.occurredAt < :threshold
    """)
    int deleteOlderThan(@Param("threshold") LocalDateTime threshold);
}
