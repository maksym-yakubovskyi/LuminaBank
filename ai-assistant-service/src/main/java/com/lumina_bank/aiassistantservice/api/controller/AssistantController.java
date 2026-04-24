package com.lumina_bank.aiassistantservice.api.controller;

import com.lumina_bank.aiassistantservice.api.response.ChatMessageResponse;
import com.lumina_bank.aiassistantservice.api.response.ConversationResponse;
import com.lumina_bank.aiassistantservice.application.assistant.history.ChatHistoryService;
import com.lumina_bank.aiassistantservice.application.mapper.ChatMessageMapper;
import com.lumina_bank.aiassistantservice.application.mapper.ConversationMapper;
import com.lumina_bank.aiassistantservice.domain.assistant.AssistantContext;
import com.lumina_bank.aiassistantservice.api.request.ChatRequest;
import com.lumina_bank.aiassistantservice.api.response.ChatResponse;
import com.lumina_bank.aiassistantservice.application.assistant.orchestrator.AssistantOrchestrator;
import com.lumina_bank.aiassistantservice.domain.model.ChatMessage;
import com.lumina_bank.aiassistantservice.domain.model.Conversation;
import com.lumina_bank.common.enums.user.Role;
import com.lumina_bank.common.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/assistant")
@RequiredArgsConstructor
@Slf4j
public class AssistantController {
    private final AssistantOrchestrator orchestrator;
    private final ChatHistoryService historyService;
    private final ChatMessageMapper messageMapper;
    private final ConversationMapper conversationMapper;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat (
            @RequestBody ChatRequest request,
            @AuthenticationPrincipal Jwt jwt){
        Long userId = JwtUtils.extractUserId(jwt);
        Role role = Role.valueOf(jwt.getClaimAsStringList("roles").getFirst());

        ChatResponse response = orchestrator.handleMessage(
                request,
                new AssistantContext(userId,role));

        return  ResponseEntity.ok().body(response);
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationResponse>> getUserConversations(@AuthenticationPrincipal Jwt jwt){
        Long userId = JwtUtils.extractUserId(jwt);

        List<Conversation> conversations = historyService.getUserConversations(userId);

        return ResponseEntity.ok().body(conversationMapper.toResponseList(conversations));
    }

    @GetMapping("/conversations/{id}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = JwtUtils.extractUserId(jwt);

        List<ChatMessage> messages = historyService.getConversationMessages(id, userId);

        return ResponseEntity.ok().body(messageMapper.toResponseList(messages));
    }


    @DeleteMapping("/conversations/{id}")
    public ResponseEntity<Void> deleteConversation(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {

        Long userId = JwtUtils.extractUserId(jwt);

        historyService.deleteConversation(id, userId);

        return ResponseEntity.noContent().build();
    }
}
