package com.lumina_bank.aiassistantservice.domain.result.data;

import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.result.AssistantData;

import java.util.Map;

public record ConfirmationSummaryData(
        Intent intent,
        Map<String,Object> params
) implements AssistantData {}
