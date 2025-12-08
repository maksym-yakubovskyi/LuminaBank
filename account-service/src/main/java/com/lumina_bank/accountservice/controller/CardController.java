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

import java.net.URI;

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
            @RequestBody Status status) {
        log.info("PUT /cards/{cardId} - Received request to change active status card with id={}", cardId);

        Card card = cardService.setActive(cardId, status);

        log.info("Active status card updated id={}, status={}", card.getId(),status);

        return ResponseEntity.ok().body(CardResponse.fromEntity(card));
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<?> getCardsByAccountId(
            @PathVariable Long accountId,
            @RequestBody Status status) {
        log.info("GET /cards/accountId} - Fetching card with accountId = {}", accountId);

        return ResponseEntity.ok().body(cardService.getCardsByAccountId(accountId,status));
    }
}
