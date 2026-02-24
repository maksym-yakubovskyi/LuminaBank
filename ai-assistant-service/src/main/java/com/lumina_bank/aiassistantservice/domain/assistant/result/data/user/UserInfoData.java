package com.lumina_bank.aiassistantservice.domain.assistant.result.data.user;

import com.lumina_bank.aiassistantservice.infrastructure.external.user.dto.UserResponse;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantData;

public record UserInfoData(
        UserResponse user
) implements AssistantData {}
