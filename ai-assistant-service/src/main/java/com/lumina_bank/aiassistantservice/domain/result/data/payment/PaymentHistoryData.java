package com.lumina_bank.aiassistantservice.domain.result.data.payment;

import com.lumina_bank.aiassistantservice.domain.dto.client.payment.TransactionHistoryItemDto;
import com.lumina_bank.aiassistantservice.domain.result.AssistantData;

import java.util.List;

public record PaymentHistoryData(
        List<TransactionHistoryItemDto> history
) implements AssistantData {}
