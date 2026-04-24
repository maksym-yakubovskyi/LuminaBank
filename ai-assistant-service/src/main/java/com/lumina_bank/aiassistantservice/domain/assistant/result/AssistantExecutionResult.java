package com.lumina_bank.aiassistantservice.domain.assistant.result;

import com.lumina_bank.aiassistantservice.domain.assistant.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.assistant.result.data.EmptyData;
import com.lumina_bank.aiassistantservice.domain.assistant.result.data.MissingParamsData;
import com.lumina_bank.aiassistantservice.domain.enums.ExecutionDirective;
import com.lumina_bank.aiassistantservice.domain.enums.ExecutionStatus;
import com.lumina_bank.aiassistantservice.domain.enums.FlowState;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;

import java.util.List;

public record AssistantExecutionResult(

        ExecutionStatus status,
        Intent intent,
        AssistantData data,
        String errorMessage,

        FlowState nextFlowState,
        Intent nextIntent,

        List<Intent> nextActions,

        ExecutionDirective directive
) {

    public static AssistantExecutionResult success(
            Intent intent,
            AssistantData data
    ) {
        return success(intent, data, List.of());
    }

    public static AssistantExecutionResult success(
            Intent intent,
            AssistantData data,
            List<Intent> nextActions
    ) {
        return new AssistantExecutionResult(
                ExecutionStatus.SUCCESS,
                intent,
                data,
                null,
                null,
                null,
                nextActions,
                ExecutionDirective.RESPOND
        );
    }

    public static AssistantExecutionResult error(
            Intent intent,
            String errorMessage
    ) {
        return new AssistantExecutionResult(
                ExecutionStatus.ERROR,
                intent,
                new EmptyData(),
                errorMessage,
                null,
                null,
                List.of(),
                ExecutionDirective.RESPOND
        );
    }

    public static AssistantExecutionResult needClarification(
            Intent intent,
            AssistantData data
    ) {
        return new AssistantExecutionResult(
                ExecutionStatus.NEED_CLARIFICATION,
                intent,
                data,
                null,
                null,
                null,
                List.of(),
                ExecutionDirective.RESPOND
        );
    }

    public static AssistantExecutionResult askParam(
            Intent intent,
            RequiredParam param
    ) {
        return needClarification(
                intent,
                new MissingParamsData(intent, List.of(param),param.name())
        );
    }

    public static AssistantExecutionResult confirmNavigation(
            Intent currentIntent,
            AssistantData data,
            Intent nextIntent
    ) {
        return new AssistantExecutionResult(
                ExecutionStatus.NEED_CONFIRMATION,
                currentIntent,
                data,
                null,
                FlowState.CONFIRM_NAVIGATION,
                nextIntent,
                List.of(),
                ExecutionDirective.RESPOND
        );
    }

    public static AssistantExecutionResult confirmFinal(
            Intent intent,
            AssistantData data
    ) {
        return new AssistantExecutionResult(
                ExecutionStatus.NEED_CONFIRMATION,
                intent,
                data,
                null,
                FlowState.CONFIRM_FINAL,
                null,
                List.of(),
                ExecutionDirective.RESPOND
        );
    }
    public static AssistantExecutionResult continueFlow(
            Intent intent,
            FlowState nextFlowState
    ) {
        return new AssistantExecutionResult(
                ExecutionStatus.SUCCESS,
                intent,
                new EmptyData(),
                null,
                nextFlowState,
                null,
                List.of(),
                ExecutionDirective.CONTINUE_FLOW
        );
    }
}

