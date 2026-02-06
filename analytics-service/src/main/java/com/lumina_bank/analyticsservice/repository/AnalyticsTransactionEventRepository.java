package com.lumina_bank.analyticsservice.repository;

import com.lumina_bank.analyticsservice.model.AnalyticsTransactionEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnalyticsTransactionEventRepository extends JpaRepository<AnalyticsTransactionEvent, Long> {
    List<AnalyticsTransactionEvent> findByUserIdAndProcessedAtBetween(
            Long userId, LocalDateTime from, LocalDateTime to
    );
}
