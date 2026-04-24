package com.lumina_bank.aiassistantservice.domain.assistant.flow.impl;

import com.lumina_bank.aiassistantservice.domain.assistant.AssistantContext;
import com.lumina_bank.aiassistantservice.domain.assistant.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.assistant.intent.IntentRegistry;
import com.lumina_bank.aiassistantservice.domain.enums.ExecutionStatus;
import com.lumina_bank.aiassistantservice.domain.enums.FlowState;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.assistant.flow.FlowHandler;
import com.lumina_bank.aiassistantservice.domain.model.Conversation;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.assistant.result.IntentResult;
import com.lumina_bank.aiassistantservice.application.assistant.IntentDetectionService;
import com.lumina_bank.aiassistantservice.application.assistant.history.ConversationStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class IdleHandler implements FlowHandler {

    private final IntentDetectionService intentDetector;
    private final ConversationStateService stateService;
    private final IntentRegistry registry;

    @Override
    public FlowState supportedState() {
        return FlowState.IDLE;
    }

    @Override
    public AssistantExecutionResult handle(Conversation c, String message, AssistantContext context) {

        IntentResult intent = intentDetector.detect(message,c.getId());

        if (intent.confidence() < 0.6 || intent.intent() == Intent.UNKNOWN) {
            return AssistantExecutionResult.error(Intent.UNKNOWN, "UNKNOWN_MESSAGE");
        }

        IntentDefinition def = registry.get(intent.intent());

        if (!def.requiresParams(context)) {

            Map<String,Object> params = new HashMap<>();

            params.put("originalMessage", message);

            stateService.initIntent(c, intent.intent(), null);

            AssistantExecutionResult result =
                    def.execute(params, c.getId(), context);

            stateService.applyExecutionResult(c, result);

            if (result.status() == ExecutionStatus.SUCCESS
                    || result.status() == ExecutionStatus.ERROR) {
                stateService.finishFlow(c);
            }

            return result;
        }

        stateService.initIntent(c, intent.intent(),FlowState.COLLECTING_PARAMS);

        return AssistantExecutionResult.continueFlow(intent.intent(), FlowState.COLLECTING_PARAMS);
    }
}