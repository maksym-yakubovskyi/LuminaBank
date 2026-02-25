package com.lumina_bank.paymentservice.infrastructure.external.NBU.dto;

import java.math.BigDecimal;

public record ExchangeRateResponse(
        String txt,
        BigDecimal rate,
        String cc,
        String exchangedate
) {
}