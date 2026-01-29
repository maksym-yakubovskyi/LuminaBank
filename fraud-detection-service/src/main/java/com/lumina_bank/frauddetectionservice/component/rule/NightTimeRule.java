package com.lumina_bank.frauddetectionservice.component.rule;

import com.lumina_bank.common.dto.event.payment_events.PaymentCreatedEvent;
import com.lumina_bank.frauddetectionservice.dto.RiskCheckResult;
import com.lumina_bank.frauddetectionservice.dto.RiskRule;
import org.springframework.stereotype.Component;

@Component
public class NightTimeRule implements RiskRule {

    @Override
    public RiskCheckResult check(PaymentCreatedEvent event) {

        int hour = event.createdAt().getHour();

        if (hour > 0 && hour < 5) {
            return new RiskCheckResult(
                    10,
                    "Transaction performed at night time"
            );
        }

        return RiskCheckResult.none();
    }
}

