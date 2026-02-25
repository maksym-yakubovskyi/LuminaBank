package com.lumina_bank.aiassistantservice.api.controller;

import com.lumina_bank.aiassistantservice.domain.assistant.AssistantContext;
import com.lumina_bank.aiassistantservice.api.request.ChatRequest;
import com.lumina_bank.aiassistantservice.api.response.ChatResponse;
import com.lumina_bank.aiassistantservice.application.assistant.orchestrator.AssistantOrchestrator;
import com.lumina_bank.common.enums.user.Role;
import com.lumina_bank.common.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assistant")
@RequiredArgsConstructor
@Slf4j
public class AssistantController {
    private final AssistantOrchestrator orchestrator;

    @PostMapping("/chat")
    public ResponseEntity<?> chat (
            @RequestBody ChatRequest request,
            @AuthenticationPrincipal Jwt jwt){
        Long userId = JwtUtils.extractUserId(jwt);
        Role role = Role.valueOf(jwt.getClaimAsStringList("roles").getFirst());

        ChatResponse response = orchestrator.handleMessage(
                request,
                new AssistantContext(userId,role));

        return  ResponseEntity.ok().body(response);
    }

}
