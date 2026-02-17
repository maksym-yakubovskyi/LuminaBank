package com.lumina_bank.aiassistantservice.domain.intent;

import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IntentDefinition {

    Intent intent();

    List<RequiredParam> requiredParams();

    AssistantExecutionResult execute(Map<String, Object> params);

    default AssistantExecutionResult execute(
            Map<String, Object> params,
            UUID conversationId
    ) {
        return execute(params);
    }

    default AssistantExecutionResult perform(Map<String,Object> params) {
        return execute(params);
    }

    default boolean requiresFinalConfirmation() {
        return false;
    }
}
