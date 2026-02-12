package com.lumina_bank.aiassistantservice.domain.flow.impl;

import com.lumina_bank.aiassistantservice.domain.flow.FlowHandler;
import com.lumina_bank.aiassistantservice.domain.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.domain.enums.ConfirmationDecision;
import com.lumina_bank.aiassistantservice.domain.enums.FlowState;
import com.lumina_bank.aiassistantservice.domain.model.Conversation;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.intent.IntentRegistry;
import com.lumina_bank.aiassistantservice.service.ai.ConfirmationExtractor;
import com.lumina_bank.aiassistantservice.service.ConversationStateService;
import com.lumina_bank.aiassistantservice.util.ParamsJsonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AwaitingFinalConfirmationHandler implements FlowHandler {

    private final ConfirmationExtractor confirmationExtractor;
    private final IntentRegistry registry;
    private final ParamsJsonMapper paramsService;
    private final ConversationStateService stateService;

    @Override
    public FlowState supportedState() {
        return FlowState.AWAITING_FINAL_CONFIRMATION;
    }

    @Override
    public AssistantExecutionResult handle(Conversation c, String message) {

        ConfirmationDecision decision  = confirmationExtractor.extractDecision(message, c.getId());

        switch (decision) {

            case CONFIRM -> {
                IntentDefinition def = registry.get(c.getActiveIntent());
                Map<String, Object> params = paramsService.read(c);

                AssistantExecutionResult result = def.perform(params);

                stateService.finishFlow(c);
                return result;
            }

            case DECLINE -> {
                stateService.finishFlow(c);

                return AssistantExecutionResult.success(
                        c.getActiveIntent(),
                        new ClarificationData("Добре, операцію скасовано.")
                );
            }

            case MODIFY -> {
                stateService.moveTo(c, FlowState.COLLECTING_PARAMS);

                return AssistantExecutionResult.needClarification(
                        c.getActiveIntent(),
                        new ClarificationData("Що саме потрібно змінити?")
                );
            }
        }

        throw new IllegalStateException("Unknown decision");
    }
}

