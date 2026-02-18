package com.lumina_bank.aiassistantservice.domain.result.data.user;

import com.lumina_bank.aiassistantservice.domain.dto.client.user.BusinessUserResponse;
import com.lumina_bank.aiassistantservice.domain.result.AssistantData;

public record BusinessUserUpdatedData(
        BusinessUserResponse user
) implements AssistantData {}
