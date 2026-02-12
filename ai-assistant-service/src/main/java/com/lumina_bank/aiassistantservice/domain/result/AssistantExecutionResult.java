package com.lumina_bank.aiassistantservice.domain.result;

import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.domain.result.data.EmptyData;
import com.lumina_bank.aiassistantservice.domain.result.data.MissingParamsData;
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
        ExecutionDirective directive
) {
    public static AssistantExecutionResult success(
            Intent intent, AssistantData data
    ) {
        return new AssistantExecutionResult(
                ExecutionStatus.SUCCESS,
                intent,
                data,
                null,
                null,
                null,
                ExecutionDirective.RESPOND
        );
    }

    public static AssistantExecutionResult error(
            Intent intent, String errorMessage
    ) {
        return new AssistantExecutionResult(
                ExecutionStatus.ERROR,
                intent,
                new EmptyData(),
                errorMessage,
                null,
                null,
                ExecutionDirective.RESPOND
        );
    }

    public static AssistantExecutionResult needClarification(
            Intent intent, AssistantData data
    ){
        return new AssistantExecutionResult(
                ExecutionStatus.NEED_CLARIFICATION,
                intent,
                data,
                null,
                null,
                null,
                ExecutionDirective.RESPOND
        );
    }

    public static AssistantExecutionResult askParam(
            Intent intent,
            RequiredParam param
    ) {
        return new AssistantExecutionResult(
                ExecutionStatus.NEED_CLARIFICATION,
                intent,
                new MissingParamsData(intent, List.of(param)),
                null,
                null,
                null,
                ExecutionDirective.RESPOND
        );
    }

    public static AssistantExecutionResult needConfirmation(
            Intent intent,
            String message,
            Intent nextIntent
    ) {
        return new AssistantExecutionResult(
                ExecutionStatus.NEED_CONFIRMATION,
                intent,
                new ClarificationData(message),
                null,
                FlowState.CONFIRMING,
                nextIntent,
                ExecutionDirective.RESPOND
        );
    }

    public static AssistantExecutionResult needConfirmation(
            Intent intent,
            AssistantData data
    ) {
        return new AssistantExecutionResult(
                ExecutionStatus.NEED_CONFIRMATION,
                intent,
                data,
                null,
                FlowState.CONFIRMING,
                intent,
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
                ExecutionDirective.CONTINUE_FLOW
        );
    }
}
