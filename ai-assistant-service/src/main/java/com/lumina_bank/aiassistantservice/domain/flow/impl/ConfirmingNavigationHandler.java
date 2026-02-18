package com.lumina_bank.aiassistantservice.domain.flow.impl;

import com.lumina_bank.aiassistantservice.domain.dto.AssistantContext;
import com.lumina_bank.aiassistantservice.domain.enums.ConfirmationDecision;
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
public class ConfirmingNavigationHandler implements FlowHandler {

    private final IntentDetectionService intentDetector;
    private final ConfirmationExtractor confirmationExtractor;
    private final ConversationStateService stateService;

    @Override
    public FlowState supportedState() {
        return FlowState.CONFIRM_NAVIGATION;
    }

    @Override
    public AssistantExecutionResult handle(Conversation c, String message, AssistantContext  context) {

        IntentResult newIntent = intentDetector.detect(message,c.getId());

        if (newIntent.intent() != Intent.UNKNOWN
                && newIntent.intent() != c.getActiveIntent()
                && newIntent.confidence() > 0.75) {

            stateService.finishFlow(c);
            stateService.initIntent(c, newIntent.intent(),FlowState.COLLECTING_PARAMS);

            return AssistantExecutionResult.continueFlow(newIntent.intent(), FlowState.COLLECTING_PARAMS);
        }

        ConfirmationDecision decision =
                confirmationExtractor.extractDecision(message, c.getId());

        switch (decision) {

            case CONFIRM -> {
                Intent next = c.getPendingIntent();

                stateService.initIntent(c, next, FlowState.COLLECTING_PARAMS);

                return AssistantExecutionResult.continueFlow(next, FlowState.COLLECTING_PARAMS);
            }

            case DECLINE -> {
                stateService.finishFlow(c);

                return AssistantExecutionResult.success(c.getActiveIntent(),
                        new ClarificationData("OPERATION_CANCELED")
                );
            }

            case MODIFY -> {
                stateService.moveTo(c, FlowState.COLLECTING_PARAMS);

                return AssistantExecutionResult.needClarification(c.getActiveIntent(),
                        new ClarificationData("WHAT_SHOULD_BE_CHANGED")
                );
            }

            case UNCERTAIN -> {
                return AssistantExecutionResult.needClarification(c.getActiveIntent(),
                        new ClarificationData("PLEASE_CONFIRM_OPERATION")
                );
            }
        }

        return AssistantExecutionResult.error(
                c.getActiveIntent(),
                "UNKNOWN_CONFIRMATION_STATE"
        );
    }
}

