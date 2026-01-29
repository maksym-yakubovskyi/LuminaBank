package com.lumina_bank.frauddetectionservice.component.rule;

import com.lumina_bank.common.dto.event.payment_events.PaymentCreatedEvent;
import com.lumina_bank.frauddetectionservice.dto.RiskCheckResult;
import com.lumina_bank.frauddetectionservice.dto.RiskRule;
import com.lumina_bank.frauddetectionservice.repository.UserFraudProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class LargeAmountRule implements RiskRule {
    private final UserFraudProfileRepository profileRepository;

    @Override
    public RiskCheckResult check(PaymentCreatedEvent event) {
        return profileRepository.findById(event.userId())
                .filter(p -> p.getAvgAmount30d() != null)
                .filter(p->
                        event.amount().compareTo(
                                p.getAvgAmount30d().multiply(BigDecimal.valueOf(5))
                        ) > 0
                )
                .map(_ -> new RiskCheckResult(
                        30,
                        "Amount significantly exceeds user's average"
                ))
                .orElse(RiskCheckResult.none());
    }
}
