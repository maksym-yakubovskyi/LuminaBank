package com.lumina_bank.aiassistantservice.domain.result.data.account;

import com.lumina_bank.aiassistantservice.domain.dto.client.account.LoanOfferResponse;
import com.lumina_bank.aiassistantservice.domain.result.AssistantData;

import java.math.BigDecimal;

public record LoanConfirmationData(
        Long accountId,
        BigDecimal amount,
        LoanOfferResponse offer
) implements AssistantData {}
