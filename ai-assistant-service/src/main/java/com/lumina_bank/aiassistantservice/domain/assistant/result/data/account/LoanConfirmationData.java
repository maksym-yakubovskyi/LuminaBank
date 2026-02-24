package com.lumina_bank.aiassistantservice.domain.assistant.result.data.account;

import com.lumina_bank.aiassistantservice.infrastructure.external.account.dto.LoanOfferResponse;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantData;

import java.math.BigDecimal;

public record LoanConfirmationData(
        Long accountId,
        BigDecimal amount,
        LoanOfferResponse offer
) implements AssistantData {}
