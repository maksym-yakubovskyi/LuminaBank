package com.lumina_bank.aiassistantservice.application.assistant.history;

import com.lumina_bank.aiassistantservice.domain.assistant.result.data.MissingParamsData;
import com.lumina_bank.aiassistantservice.domain.enums.FlowState;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.model.Conversation;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantExecutionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConversationStateService {

    private final ChatHistoryService history;

    @Transactional
    public void moveTo(Conversation c, FlowState newState) {
        c.setFlowState(newState);
        history.saveConversation(c);
    }

    @Transactional
    public void initIntent(Conversation c, Intent intent,FlowState state) {
        c.setActiveIntent(intent);
        c.setFlowState(state);
        c.setCollectedParamsJson("{}");
        history.saveConversation(c);
    }

    @Transactional
    public void finishFlow(Conversation c) {
        c.setFlowState(FlowState.IDLE);
        c.setActiveIntent(null);
        c.setCollectedParamsJson(null);
        c.setPendingIntent(null);
        history.saveConversation(c);
    }

    @Transactional
    public void applyExecutionResult(Conversation c, AssistantExecutionResult r) {
        if(r.data() instanceof MissingParamsData data){
            c.setAwaitingParam(data.currentParam());
        } else {
            c.setAwaitingParam(null);
        }

        if (r.nextIntent() != null) {
            c.setPendingIntent(r.nextIntent());
        }
        if (r.nextFlowState() != null) {
            c.setFlowState(r.nextFlowState());
        }
        history.saveConversation(c);
    }
}

