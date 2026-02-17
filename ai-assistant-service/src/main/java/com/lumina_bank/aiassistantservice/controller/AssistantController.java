package com.lumina_bank.aiassistantservice.controller;

import com.lumina_bank.aiassistantservice.domain.dto.ChatRequest;
import com.lumina_bank.aiassistantservice.domain.dto.ChatResponse;
import com.lumina_bank.aiassistantservice.service.orchestrator.AssistantOrchestrator;
import com.lumina_bank.common.exception.JwtMissingException;
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
        if (jwt == null) throw new JwtMissingException("JWT token is required");
        Long userId = Long.valueOf(jwt.getSubject());

        ChatResponse response = orchestrator.handleMessage(request,userId);

        return  ResponseEntity.ok().body(response);
    }

}
