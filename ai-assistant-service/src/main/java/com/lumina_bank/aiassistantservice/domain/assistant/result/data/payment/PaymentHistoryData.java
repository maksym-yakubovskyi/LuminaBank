package com.lumina_bank.aiassistantservice.domain.assistant.result.data.payment;

import com.lumina_bank.aiassistantservice.infrastructure.external.payment.dto.TransactionHistoryItemResponse;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantData;

import java.util.List;

public record PaymentHistoryData(
        List<TransactionHistoryItemResponse> history
) implements AssistantData {}
