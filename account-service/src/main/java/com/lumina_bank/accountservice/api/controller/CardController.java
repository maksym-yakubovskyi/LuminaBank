package com.lumina_bank.accountservice.api.controller;

import com.lumina_bank.accountservice.api.request.CardCreateRequest;
import com.lumina_bank.accountservice.api.response.CardResponse;
import com.lumina_bank.accountservice.application.mapper.CardMapper;
import com.lumina_bank.accountservice.domain.enums.Status;
import com.lumina_bank.accountservice.domain.model.Card;
import com.lumina_bank.accountservice.application.service.CardService;
import com.lumina_bank.common.security.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.net.URI;
import java.util.List;

import org.springframework.security.oauth2.jwt.Jwt;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
@Slf4j
public class CardController {
    private final CardService cardService;
    private final CardMapper cardMapper;

    @PostMapping("/{accountId}")
    public ResponseEntity<CardResponse> addCard(
            @PathVariable Long accountId,
            @Valid @RequestBody CardCreateRequest request) {

        Card card = cardService.createCard(accountId, request);

        log.info("Card created cardId={} accountId={} type={}", card.getId(), accountId, card.getCardType());

        return ResponseEntity.created(URI.create("/cards/" + accountId))
                .body(cardMapper.toResponse(card));
    }

    @PutMapping("/{cardId}/status")
    public ResponseEntity<CardResponse> changeStatus(@PathVariable Long cardId, @RequestBody Status status) {
        Card card = cardService.setStatus(cardId, status);

        log.info("Card status changed cardId={} newStatus={}", cardId, status);

        return ResponseEntity.ok().body(cardMapper.toResponse(card));
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<List<CardResponse>> getByAccount(@PathVariable Long accountId) {
        List<Card> cards = cardService.getCardsByAccountId(accountId);

        log.debug("Fetched cards for accountId={} count={}", accountId, cards.size());

        return ResponseEntity.ok().body(cardMapper.toResponseList(cards));
    }

    @GetMapping("/my")
    public ResponseEntity<List<CardResponse>> getMyCards(@AuthenticationPrincipal Jwt jwt) {
        Long userId = JwtUtils.extractUserId(jwt);

        List<Card> cards = cardService.getCardsByUserId(userId);

        log.debug("Fetched cards for userId={} count={}", userId, cards.size());

        return ResponseEntity.ok( cardMapper.toResponseList(cards));
    }
}