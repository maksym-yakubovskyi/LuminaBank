package com.lumina_bank.frauddetectionservice.dto;

import com.lumina_bank.common.dto.event.payment_events.PaymentCreatedEvent;

public interface RiskRule {
    RiskCheckResult check(PaymentCreatedEvent  event);
}
