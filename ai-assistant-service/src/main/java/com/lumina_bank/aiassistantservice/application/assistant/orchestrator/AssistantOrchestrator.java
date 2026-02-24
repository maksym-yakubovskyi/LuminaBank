package com.lumina_bank.aiassistantservice.application.assistant.orchestrator;

import com.lumina_bank.aiassistantservice.api.request.ChatRequest;
import com.lumina_bank.aiassistantservice.api.response.ChatResponse;
import com.lumina_bank.aiassistantservice.application.assistant.history.ChatHistoryService;
import com.lumina_bank.aiassistantservice.domain.assistant.AssistantContext;
import com.lumina_bank.aiassistantservice.domain.enums.ExecutionStatus;
import com.lumina_bank.aiassistantservice.domain.enums.MessageType;
import com.lumina_bank.aiassistantservice.domain.assistant.flow.FlowRouter;
import com.lumina_bank.aiassistantservice.domain.model.Conversation;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.assistant.result.data.info.FinalResponseData;
import com.lumina_bank.aiassistantservice.application.assistant.ResponseGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssistantOrchestrator {
    private final ChatHistoryService history;
    private final FlowRouter flowRouter;
    private final ResponseGenerationService responses;

    public ChatResponse handleMessage(ChatRequest request, AssistantContext context) {

        Conversation conversation =
                history.getOrCreateConversation(request.conversationIdAsUuid(), context.userId());

        history.saveUserMessage(conversation, request.message());

        AssistantExecutionResult result =
                flowRouter.handle(conversation, request.message(),context);

        String text;

        if(result.data() instanceof FinalResponseData(String finalText)){
            text = finalText;
        }else{
            text = responses.generateResponse(
                    request.message(),
                    result,
                    conversation.getId(),
                    context);
        }

        history.saveAssistantMessage(
                conversation,
                mapType(result.status()),
                text
        );

        return new ChatResponse(
                mapType(result.status()).name(),
                text,
                conversation.getId().toString()
        );
    }


    private MessageType mapType(ExecutionStatus status) {
        return switch (status) {
            case SUCCESS -> MessageType.INFO;
            case NEED_CLARIFICATION, NEED_CONFIRMATION -> MessageType.CONFIRM;
            case ERROR -> MessageType.ERROR;
        };
    }
}
