package com.lumina_bank.paymentservice.domain.repository;

import com.lumina_bank.paymentservice.domain.model.PaymentTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTemplateRepository extends JpaRepository<PaymentTemplate, Long> {
    List<PaymentTemplate> findByIsRecurringTrueAndIsActiveTrueAndNextExecutionTimeBefore(LocalDateTime time);

    Optional<PaymentTemplate> findByIdAndIsActiveTrue(Long paymentTemplateId);

    List<PaymentTemplate> findAllByUserIdAndIsActiveTrue(Long userId);

    @Query("""
    SELECT t FROM PaymentTemplate t
    WHERE t.isActive = true
    AND t.isRecurring = true
    AND t.nextExecutionTime BETWEEN :now AND :reminderTime
    AND (t.lastReminderSentAt IS NULL OR t.lastReminderSentAt < :windowStart)
""")
    List<PaymentTemplate> findTemplatesForReminder(
            LocalDateTime now,
            LocalDateTime reminderTime,
            LocalDateTime windowStart
    );
}