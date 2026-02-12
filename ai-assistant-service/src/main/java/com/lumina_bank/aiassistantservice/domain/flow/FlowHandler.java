package com.lumina_bank.aiassistantservice.domain.flow;

import com.lumina_bank.aiassistantservice.domain.enums.FlowState;
import com.lumina_bank.aiassistantservice.domain.model.Conversation;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;

public interface FlowHandler {

    FlowState supportedState();

    AssistantExecutionResult handle(Conversation conversation, String message);
}
