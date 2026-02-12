package com.lumina_bank.aiassistantservice.domain.flow.impl;

import com.lumina_bank.aiassistantservice.domain.flow.FlowHandler;
import com.lumina_bank.aiassistantservice.domain.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.domain.enums.FlowState;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.model.Conversation;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.result.IntentResult;
import com.lumina_bank.aiassistantservice.service.ai.ConfirmationExtractor;
import com.lumina_bank.aiassistantservice.service.ai.IntentDetectionService;
import com.lumina_bank.aiassistantservice.service.ConversationStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConfirmingHandler implements FlowHandler {

    private final IntentDetectionService intentDetector;
    private final ConfirmationExtractor confirmationExtractor;
    private final ConversationStateService stateService;

    @Override
    public FlowState supportedState() {
        return FlowState.CONFIRMING;
    }

    @Override
    public AssistantExecutionResult handle(Conversation c, String message) {

        IntentResult newIntent = intentDetector.detect(message);

        if (newIntent.intent() != Intent.UNKNOWN
                && newIntent.intent() != c.getActiveIntent()
                && newIntent.confidence() > 0.75) {

            stateService.finishFlow(c);
            stateService.initIntent(c, newIntent.intent(),FlowState.COLLECTING_PARAMS);

            return AssistantExecutionResult.continueFlow(newIntent.intent(), FlowState.COLLECTING_PARAMS);
        }

        boolean confirmed =
                confirmationExtractor.extractConfirmation(message, c.getId());

        if (confirmed) {
            Intent next = c.getPendingIntent();
            stateService.initIntent(c, next,FlowState.COLLECTING_PARAMS);

            return AssistantExecutionResult.continueFlow(next, FlowState.COLLECTING_PARAMS);
        }

        stateService.finishFlow(c);

        return AssistantExecutionResult.success(
                c.getActiveIntent(),
                new ClarificationData("Операцію скасовано.")
        );
    }
}

