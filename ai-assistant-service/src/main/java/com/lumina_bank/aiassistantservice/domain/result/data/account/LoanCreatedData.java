package com.lumina_bank.aiassistantservice.domain.result.data.account;

import com.lumina_bank.aiassistantservice.domain.dto.client.account.LoanResponse;
import com.lumina_bank.aiassistantservice.domain.result.AssistantData;

public record LoanCreatedData(
        LoanResponse  loan
) implements AssistantData {}
