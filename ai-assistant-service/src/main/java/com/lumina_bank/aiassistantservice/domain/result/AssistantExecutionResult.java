package com.lumina_bank.aiassistantservice.domain.result;

import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
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
        return respond(
                ExecutionStatus.SUCCESS,
                intent,
                data,
                null
        );
    }

    public static AssistantExecutionResult error(
            Intent intent, String errorMessage
    ) {
        return respond(
                ExecutionStatus.ERROR,
                intent,
                new EmptyData(),
                errorMessage
        );
    }

    public static AssistantExecutionResult needClarification(
            Intent intent, AssistantData data
    ){
        return respond(
                ExecutionStatus.NEED_CLARIFICATION,
                intent,
                data,
                null
        );
    }

    public static AssistantExecutionResult askParam(
            Intent intent,
            RequiredParam param
    ) {
        return needClarification(
                intent,
                new MissingParamsData(intent, List.of(param))
        );
    }

    public static AssistantExecutionResult confirmNavigation(
            Intent currentIntent,
            AssistantData data,
            Intent nextIntent
    ) {
        return confirmation(
                currentIntent,
                data,
                FlowState.CONFIRM_NAVIGATION,
                nextIntent
        );
    }

    public static AssistantExecutionResult confirmFinal(
            Intent intent,
            AssistantData data
    ) {
        return confirmation(
                intent,
                data,
                FlowState.CONFIRM_FINAL,
                null
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

    private static AssistantExecutionResult respond(
            ExecutionStatus status,
            Intent intent,
            AssistantData data,
            String error
    ) {
        return new AssistantExecutionResult(
                status,
                intent,
                data,
                error,
                null,
                null,
                ExecutionDirective.RESPOND
        );
    }

    private static AssistantExecutionResult confirmation(
            Intent intent,
            AssistantData data,
            FlowState flowState,
            Intent nextIntent
    ) {
        return new AssistantExecutionResult(
                ExecutionStatus.NEED_CONFIRMATION,
                intent,
                data,
                null,
                flowState,
                nextIntent,
                ExecutionDirective.RESPOND
        );
    }
}

