package com.lumina_bank.paymentservice.dto;

import java.math.BigDecimal;

public record ExchangeRateResponse(
        String txt,
        BigDecimal rate,
        String cc,
        String exchangedate
) {
}