package com.lumina_bank.paymentservice.application.mapper;

import com.lumina_bank.common.dto.event.payment_events.PaymentCreatedEvent;
import com.lumina_bank.paymentservice.domain.model.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentCreatedEventMapper {

    public PaymentCreatedEvent to(Payment payment) {
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
