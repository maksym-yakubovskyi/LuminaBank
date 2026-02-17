package com.lumina_bank.aiassistantservice.service;

import com.lumina_bank.aiassistantservice.domain.enums.FlowState;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.model.Conversation;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConversationStateService {

    private final ChatHistoryService history;

    public void moveTo(Conversation c, FlowState newState) {
        c.setFlowState(newState);
        history.saveConversation(c);
    }

    public void initIntent(Conversation c, Intent intent,FlowState state) {
        c.setActiveIntent(intent);
        c.setFlowState(state);
        c.setCollectedParamsJson("{}");
        history.saveConversation(c);
    }

    public void finishFlow(Conversation c) {
        c.setFlowState(FlowState.IDLE);
        c.setActiveIntent(null);
        c.setCollectedParamsJson(null);
        c.setPendingIntent(null);
        history.saveConversation(c);
    }

    public void applyExecutionResult(Conversation c, AssistantExecutionResult r) {
        if (r.nextIntent() != null) {
            c.setPendingIntent(r.nextIntent());
        }
        if (r.nextFlowState() != null) {
            c.setFlowState(r.nextFlowState());
        }
        history.saveConversation(c);
    }
}

