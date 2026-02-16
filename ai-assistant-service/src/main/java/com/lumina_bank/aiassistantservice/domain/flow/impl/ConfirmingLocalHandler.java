package com.lumina_bank.aiassistantservice.domain.flow.impl;

import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.enums.ConfirmationDecision;
import com.lumina_bank.aiassistantservice.domain.enums.FlowState;
import com.lumina_bank.aiassistantservice.domain.enums.ParamType;
import com.lumina_bank.aiassistantservice.domain.flow.FlowHandler;
import com.lumina_bank.aiassistantservice.domain.model.Conversation;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.service.ConversationStateService;
import com.lumina_bank.aiassistantservice.service.ai.ConfirmationExtractor;
import com.lumina_bank.aiassistantservice.util.ParamsJsonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ConfirmingLocalHandler implements FlowHandler {
    private final ConfirmationExtractor confirmationExtractor;
    private final ConversationStateService stateService;
    private final ParamsJsonMapper paramsService;

    @Override
    public FlowState supportedState() {
        return FlowState.CONFIRM_LOCAL;
    }

    @Override
    public AssistantExecutionResult handle(Conversation c, String message) {

        ConfirmationDecision decision =
                confirmationExtractor.extractDecision(message, c.getId());

        switch (decision) {

            case CONFIRM -> {
                // користувач хоче додати опис
                stateService.moveTo(c, FlowState.COLLECTING_PARAMS);

                return AssistantExecutionResult.askParam(
                        c.getActiveIntent(),
                        new RequiredParam(
                                "description",
                                ParamType.STRING,
                                List.of(),
                                "Optional payment description."
                        )
                );
            }

            case DECLINE -> {
                // користувач не хоче опис
                Map<String, Object> params = paramsService.read(c);
                params.put("description", null);
                paramsService.merge(c, params);

                stateService.moveTo(c, FlowState.COLLECTING_PARAMS);

                return AssistantExecutionResult.continueFlow(
                        c.getActiveIntent(),
                        FlowState.COLLECTING_PARAMS
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
                        new ClarificationData("PLEASE_CONFIRM_DESCRIPTION")
                );
            }
        }

        return AssistantExecutionResult.error(
                c.getActiveIntent(),
                "UNKNOWN_CONFIRMATION_STATE"
        );
    }
}
