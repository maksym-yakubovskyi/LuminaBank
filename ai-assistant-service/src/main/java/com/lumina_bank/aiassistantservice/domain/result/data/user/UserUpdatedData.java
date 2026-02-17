package com.lumina_bank.aiassistantservice.domain.result.data.user;

import com.lumina_bank.aiassistantservice.domain.dto.client.user.UserResponse;
import com.lumina_bank.aiassistantservice.domain.result.AssistantData;

public record UserUpdatedData(
        UserResponse updated
) implements AssistantData {}
