package com.lumina_bank.aiassistantservice.domain.assistant.intent;

import com.lumina_bank.aiassistantservice.domain.assistant.AssistantContext;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.assistant.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IntentDefinition {

    Intent intent();

    List<RequiredParam> requiredParams(AssistantContext context);

    AssistantExecutionResult execute(
            Map<String, Object> params,
            UUID conversationId,
            AssistantContext context
    );

    default AssistantExecutionResult perform(
            Map<String,Object> params,
            AssistantContext context
    ) {
        return execute(params, null, context);
    }
    default boolean requiresFinalConfirmation() {
        return false;
    }
}
