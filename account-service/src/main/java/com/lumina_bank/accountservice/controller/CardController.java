package com.lumina_bank.accountservice.controller;

import com.lumina_bank.accountservice.dto.CardCreateDto;
import com.lumina_bank.accountservice.dto.CardResponse;
import com.lumina_bank.accountservice.enums.Status;
import com.lumina_bank.accountservice.model.Card;
import com.lumina_bank.accountservice.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.net.URI;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import com.lumina_bank.common.exception.JwtMissingException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
@Slf4j
public class CardController {
    private final CardService cardService;

    @PostMapping("/{accountId}")
    public ResponseEntity<?> addCard(
            @PathVariable Long accountId,
            @Valid @RequestBody CardCreateDto cardCreateDto) {
        log.info("POST /cards/{accountId} - Received request to create card : {}", accountId);

        Card card = cardService.createCard(accountId, cardCreateDto);

        log.info("Card created with id : {}", card.getId());

        return ResponseEntity.created(URI.create("/cards/" + accountId))
                .body(CardResponse.fromEntity(card));
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<?> setActiveCard(
            @PathVariable Long cardId,
            @RequestParam(required = false) Status status) {
        log.info("PUT /cards/{cardId} - Received request to change active status card with id={}", cardId);

        Card card = cardService.setActive(cardId, status);

        log.info("Active status card updated id={}, status={}", card.getId(),status);

        return ResponseEntity.ok().body(CardResponse.fromEntity(card));
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<?> getCardsByAccountId(
            @PathVariable Long accountId) {
        log.info("GET /cards/accountId} - Fetching card with accountId = {}", accountId);

        return ResponseEntity.ok().body(cardService.getCardsByAccountId(accountId));
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyCards(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) throw new JwtMissingException("JWT token is required");
        Long userId = Long.valueOf(jwt.getSubject());

        log.info("GET /cards/my - Fetching cards for userId={}", userId);

        return ResponseEntity.ok(cardService.getCardsByUserId(userId));
    }
}