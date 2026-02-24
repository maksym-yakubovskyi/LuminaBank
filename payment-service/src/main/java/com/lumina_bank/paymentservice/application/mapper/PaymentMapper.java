package com.lumina_bank.paymentservice.application.mapper;

import com.lumina_bank.paymentservice.api.response.PaymentResponse;
import com.lumina_bank.paymentservice.domain.model.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment){
        if(payment==null) return null;

        return PaymentResponse.builder()
                .id(payment.getId())
                .fromCardNumber(payment.getFromCardNumber())
                .toCardNumber(payment.getToCardNumber())
                .amount(payment.getAmount())
                .description(payment.getDescription())
                .status(payment.getPaymentStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
