package com.lumina_bank.paymentservice.service.util;

import com.lumina_bank.common.dto.event.payment_events.PaymentCreatedEvent;
import com.lumina_bank.paymentservice.model.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentCreatedEventFactory {

    public PaymentCreatedEvent from(Payment payment) {
        return new PaymentCreatedEvent(
                payment.getId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getFromCardNumber(),
                payment.getToCardNumber(),
                payment.getCreatedAt()
        );
    }
}
