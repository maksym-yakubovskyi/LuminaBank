package com.lumina_bank.aiassistantservice.domain.assistant.flow.impl;

import com.lumina_bank.aiassistantservice.domain.assistant.AssistantContext;
import com.lumina_bank.aiassistantservice.domain.assistant.flow.FlowHandler;
import com.lumina_bank.aiassistantservice.domain.assistant.result.data.ConfirmationSummaryData;
import com.lumina_bank.aiassistantservice.domain.enums.ExecutionStatus;
import com.lumina_bank.aiassistantservice.domain.enums.FlowState;
import com.lumina_bank.aiassistantservice.domain.model.Conversation;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.assistant.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.assistant.intent.IntentRegistry;
import com.lumina_bank.aiassistantservice.application.assistant.ParameterExtractionService;
import com.lumina_bank.aiassistantservice.application.assistant.history.ConversationStateService;
import com.lumina_bank.aiassistantservice.application.assistant.params.ParamsJsonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CollectingParamsHandler implements FlowHandler {

    private final IntentRegistry registry;
    private final ParameterExtractionService extractor;
    private final ParamsJsonMapper paramsService;
    private final ConversationStateService stateService;

    @Override
    public FlowState supportedState() {
        return FlowState.COLLECTING_PARAMS;
    }

    @Override
    public AssistantExecutionResult handle(Conversation c, String message, AssistantContext context) {

        IntentDefinition def = registry.get(c.getActiveIntent());

        Map<String, Object> extracted =
                extractor.extract(message, c.getActiveIntent(), def.requiredParams(context), c.getAwaitingParam(), c.getId());

        Map<String, Object> params = paramsService.merge(c, extracted);

        params.put("originalMessage", message);

        AssistantExecutionResult result = def.execute(params, c.getId(),context);

        if (result.status() == ExecutionStatus.SUCCESS && def.requiresFinalConfirmation()) {

            stateService.moveTo(c, FlowState.CONFIRM_FINAL);

            return AssistantExecutionResult.confirmFinal(
                    def.intent(),
                    new ConfirmationSummaryData(def.intent(), params)
            );
        }

        stateService.applyExecutionResult(c, result);

        if (result.status() == ExecutionStatus.SUCCESS
                || result.status() == ExecutionStatus.ERROR) {
            stateService.finishFlow(c);
        }

        return result;
    }
}