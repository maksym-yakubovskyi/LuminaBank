package com.lumina_bank.paymentservice.domain.util;

import org.springframework.stereotype.Component;

@Component
public class PaymentDescriptionBuilder {

    public String buildServiceDescription(String desc, String ref) {
        return (desc == null || desc.isBlank() ? "Service payment" : desc)
                + " | Ref: " + ref;
    }
}
