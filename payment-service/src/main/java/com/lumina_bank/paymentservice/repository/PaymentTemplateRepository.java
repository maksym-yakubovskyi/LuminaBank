package com.lumina_bank.paymentservice.repository;

import com.lumina_bank.paymentservice.model.PaymentTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTemplateRepository extends JpaRepository<PaymentTemplate, Long> {
    List<PaymentTemplate> findByIsRecurringTrueAndIsActiveTrueAndNextExecutionTimeBefore(LocalDateTime time);

    Optional<PaymentTemplate> findByIdAndIsActiveTrue(Long paymentTemplateId);

    List<PaymentTemplate> findAllByUserIdAndIsActiveTrue(Long userId);
}