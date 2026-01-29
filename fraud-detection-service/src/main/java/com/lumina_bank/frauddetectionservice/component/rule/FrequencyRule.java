package com.lumina_bank.frauddetectionservice.component.rule;

import com.lumina_bank.common.dto.event.payment_events.PaymentCreatedEvent;
import com.lumina_bank.frauddetectionservice.dto.RiskCheckResult;
import com.lumina_bank.frauddetectionservice.dto.RiskRule;
import com.lumina_bank.frauddetectionservice.repository.UserPaymentActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class FrequencyRule implements RiskRule {

    private static final int MAX_TX = 5;

    private final UserPaymentActivityRepository repository;

    @Override
    public RiskCheckResult check(PaymentCreatedEvent event) {

        LocalDateTime from = event.createdAt().minusMinutes(10);

        long count = repository
                .countByUserIdAndOccurredAtAfter(
                        event.userId(),
                        from
                );

        if (count > MAX_TX) {
            return new RiskCheckResult(
                    25,
                    "High transaction frequency in short time window"
            );
        }

        return RiskCheckResult.none();
    }
}
