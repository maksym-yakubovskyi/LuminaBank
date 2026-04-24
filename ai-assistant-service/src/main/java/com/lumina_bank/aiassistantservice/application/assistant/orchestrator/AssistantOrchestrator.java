package com.lumina_bank.aiassistantservice.application.assistant.orchestrator;

import com.lumina_bank.aiassistantservice.api.request.ChatRequest;
import com.lumina_bank.aiassistantservice.api.response.ChatResponse;
import com.lumina_bank.aiassistantservice.application.assistant.history.ChatHistoryService;
import com.lumina_bank.aiassistantservice.domain.assistant.AssistantContext;
import com.lumina_bank.aiassistantservice.domain.enums.ConversationStatus;
import com.lumina_bank.aiassistantservice.domain.enums.ExecutionStatus;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
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

        if(conversation.getStatus() == ConversationStatus.CLOSED){
            String closedMessage =
                    "Історія цього чату вичерпана. Будь ласка, створіть нову розмову.";

            return new ChatResponse(
                    MessageType.CHAT_CLOSED,
                    closedMessage,
                    conversation.getId().toString()
            );
        }

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
        MessageType type = mapType(result.intent(), result.status());

        history.saveAssistantMessage(
                conversation,
                type,
                text
        );

        history.checkAndCloseIfTooLarge(conversation);

        return new ChatResponse(
                type,
                text,
                conversation.getId().toString()
        );
    }


    private MessageType mapType(Intent intent, ExecutionStatus status) {

        if(intent == null){
            return MessageType.INFO;
        }

        if (status == ExecutionStatus.ERROR) {
            return MessageType.ERROR;
        }

        if (status == ExecutionStatus.NEED_CONFIRMATION ||
                status == ExecutionStatus.NEED_CLARIFICATION) {
            return MessageType.CONFIRM;
        }

        return switch (intent) {

            case CHECK_BALANCE,
                 CREATE_ACCOUNT,
                 CREATE_CARD,
                 TRANSFER_BY_CARD,
                 PAYMENT_SERVICE_PROVIDER,
                 PAYMENT_BY_TEMPLATE,
                 CREATE_LOAN -> MessageType.ACTION;

            case ANALYTICS_MONTHLY,
                 ANALYTICS_DAILY,
                 ANALYTICS_BY_CATEGORY,
                 ANALYTICS_TOP_RECIPIENTS,
                 ANALYTICS_FORECAST -> MessageType.ANALYTICS;

            case ANALYTICS_RECOMMENDATIONS -> MessageType.ADVICE;

            case KNOWLEDGE_QUERY,
                 ASSISTANT_INFO,
                 OUT_OF_SCOPE,
                 USER_INFORMATION,
                 LIST_ACCOUNTS,
                 LIST_CARDS,
                 LIST_LOANS -> MessageType.INFO;

            default -> MessageType.INFO;
        };
    }
}
