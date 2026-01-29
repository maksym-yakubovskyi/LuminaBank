package com.lumina_bank.frauddetectionservice.component.rule;

import com.lumina_bank.common.dto.event.payment_events.PaymentCreatedEvent;
import com.lumina_bank.frauddetectionservice.dto.RiskCheckResult;
import com.lumina_bank.frauddetectionservice.dto.RiskRule;
import com.lumina_bank.frauddetectionservice.model.UserRecipientStats;
import com.lumina_bank.frauddetectionservice.repository.UserRecipientStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class NewRecipientRule implements RiskRule {
    private static final BigDecimal THRESHOLD =
            BigDecimal.valueOf(5000);

    private final UserRecipientStatsRepository recipientStatsRepository;

    @Override
    public RiskCheckResult check(PaymentCreatedEvent event) {
        return recipientStatsRepository.findByUserIdAndToCardNumber(
                event.userId(),
                event.toCardNumber()
        )
                .filter(UserRecipientStats::isNewRecipient)
                .filter(_ -> event.amount().compareTo(THRESHOLD) > 0)
                .map(_ -> new RiskCheckResult(
                        20,
                        "New recipient with large amount"
                ))
                .orElse(RiskCheckResult.none());
    }
}
