package com.lumina_bank.aiassistantservice.domain.assistant.result.data.user;

import com.lumina_bank.aiassistantservice.infrastructure.external.user.dto.BusinessUserResponse;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantData;

public record BusinessUserUpdatedData(
        BusinessUserResponse user
) implements AssistantData {}
