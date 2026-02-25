package com.lumina_bank.analyticsservice.domain.repository;

import com.lumina_bank.analyticsservice.domain.model.AnalyticsTopRecipients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnalyticsTopRecipientsRepository extends JpaRepository<AnalyticsTopRecipients, Long> {

    Optional<AnalyticsTopRecipients> findByUserIdAndRecipientId(Long userId, Long toAccountOwnerId);

    List<AnalyticsTopRecipients> findTop5ByUserIdOrderByTotalAmountDesc(Long userId);
}
