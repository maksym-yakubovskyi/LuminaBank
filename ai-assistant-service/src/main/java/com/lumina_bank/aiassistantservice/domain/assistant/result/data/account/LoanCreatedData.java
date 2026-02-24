package com.lumina_bank.aiassistantservice.domain.assistant.result.data.account;

import com.lumina_bank.aiassistantservice.infrastructure.external.account.dto.LoanResponse;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantData;

public record LoanCreatedData(
        LoanResponse  loan
) implements AssistantData {}
