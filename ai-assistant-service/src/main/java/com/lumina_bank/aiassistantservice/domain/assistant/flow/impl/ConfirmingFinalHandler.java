package com.lumina_bank.aiassistantservice.domain.assistant.flow.impl;

import com.lumina_bank.aiassistantservice.domain.assistant.AssistantContext;
import com.lumina_bank.aiassistantservice.domain.assistant.flow.FlowHandler;
import com.lumina_bank.aiassistantservice.domain.assistant.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.domain.enums.ConfirmationDecision;
import com.lumina_bank.aiassistantservice.domain.enums.FlowState;
import com.lumina_bank.aiassistantservice.domain.model.Conversation;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.assistant.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.assistant.intent.IntentRegistry;
import com.lumina_bank.aiassistantservice.application.assistant.ConfirmationExtractor;
import com.lumina_bank.aiassistantservice.application.assistant.history.ConversationStateService;
import com.lumina_bank.aiassistantservice.application.assistant.params.ParamsJsonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ConfirmingFinalHandler implements FlowHandler {

    private final ConfirmationExtractor confirmationExtractor;
    private final IntentRegistry registry;
    private final ParamsJsonMapper paramsService;
    private final ConversationStateService stateService;

    @Override
    public FlowState supportedState() {
        return FlowState.CONFIRM_FINAL;
    }

    @Override
    public AssistantExecutionResult handle(Conversation c, String message, AssistantContext context) {

        ConfirmationDecision decision  = confirmationExtractor.extractDecision(message, c.getId());

        switch (decision) {

            case CONFIRM -> {
                IntentDefinition def = registry.get(c.getActiveIntent());
                Map<String, Object> params = paramsService.read(c);

                AssistantExecutionResult result = def.perform(params,context);

                stateService.finishFlow(c);
                return result;
            }

            case DECLINE -> {
                stateService.finishFlow(c);

                return AssistantExecutionResult.success(
                        c.getActiveIntent(),
                        new ClarificationData("OPERATION_CANCELED")
                );
            }

            case MODIFY -> {
                stateService.moveTo(c, FlowState.COLLECTING_PARAMS);

                return AssistantExecutionResult.needClarification(
                        c.getActiveIntent(),
                        new ClarificationData("WHAT_SHOULD_BE_CHANGED")
                );
            }

            case UNCERTAIN -> {
                return AssistantExecutionResult.needClarification(
                        c.getActiveIntent(),
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

