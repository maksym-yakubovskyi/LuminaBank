package com.lumina_bank.aiassistantservice.domain.flow;

import com.lumina_bank.aiassistantservice.domain.dto.AssistantContext;
import com.lumina_bank.aiassistantservice.domain.enums.ExecutionDirective;
import com.lumina_bank.aiassistantservice.domain.enums.FlowState;
import com.lumina_bank.aiassistantservice.domain.model.Conversation;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class FlowRouter {

    private final Map<FlowState, FlowHandler> handlers;

    @Autowired
    public FlowRouter(List<FlowHandler> handlerList) {

        this.handlers = handlerList.stream()
                .collect(Collectors.toUnmodifiableMap(
                        FlowHandler::supportedState,
                        Function.identity()
                ));
    }

    public AssistantExecutionResult handle(Conversation c, String message, AssistantContext context) {
        AssistantExecutionResult result;
        int guard = 0;
        do {
            if (++guard > 10) {
                throw new IllegalStateException("Flow loop detected");
            }

            FlowHandler handler = handlers.get(c.getFlowState());
            result = handler.handle(c, message,context);

        } while (result.directive() == ExecutionDirective.CONTINUE_FLOW);

        return result;
    }
}
