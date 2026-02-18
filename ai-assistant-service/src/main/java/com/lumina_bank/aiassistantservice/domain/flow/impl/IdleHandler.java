package com.lumina_bank.aiassistantservice.domain.flow.impl;

import com.lumina_bank.aiassistantservice.domain.dto.AssistantContext;
import com.lumina_bank.aiassistantservice.domain.enums.FlowState;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.flow.FlowHandler;
import com.lumina_bank.aiassistantservice.domain.model.Conversation;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.result.IntentResult;
import com.lumina_bank.aiassistantservice.service.ai.IntentDetectionService;
import com.lumina_bank.aiassistantservice.service.ConversationStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IdleHandler implements FlowHandler {

    private final IntentDetectionService intentDetector;
    private final ConversationStateService stateService;

    @Override
    public FlowState supportedState() {
        return FlowState.IDLE;
    }

    @Override
    public AssistantExecutionResult handle(Conversation c, String message, AssistantContext context) {

        IntentResult intent = intentDetector.detect(message,c.getId());

        if (intent.confidence() < 0.8) {
            return AssistantExecutionResult.error(Intent.UNKNOWN, "UNKNOWN_MESSAGE");
        }

        stateService.initIntent(c, intent.intent(),FlowState.COLLECTING_PARAMS);

        return AssistantExecutionResult.continueFlow(intent.intent(), FlowState.COLLECTING_PARAMS);
    }
}

