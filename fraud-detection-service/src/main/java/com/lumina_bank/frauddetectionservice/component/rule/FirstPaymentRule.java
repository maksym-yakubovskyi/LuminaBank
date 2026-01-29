package com.lumina_bank.frauddetectionservice.component.rule;

import com.lumina_bank.common.dto.event.payment_events.PaymentCreatedEvent;
import com.lumina_bank.frauddetectionservice.dto.RiskCheckResult;
import com.lumina_bank.frauddetectionservice.dto.RiskRule;
import com.lumina_bank.frauddetectionservice.model.UserFraudProfile;
import com.lumina_bank.frauddetectionservice.repository.UserFraudProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FirstPaymentRule implements RiskRule {

    private final UserFraudProfileRepository repository;

    @Override
    public RiskCheckResult check(PaymentCreatedEvent event) {
        return repository.findById(event.userId())
                .filter(UserFraudProfile::isFirstPayment)
                .map(_ -> new RiskCheckResult(
                        15,
                        "First payment for this user"
                ))
                .orElse(RiskCheckResult.none());
    }
}
