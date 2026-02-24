package com.lumina_bank.accountservice.application.mapper;

import com.lumina_bank.accountservice.api.response.CardResponse;
import com.lumina_bank.accountservice.domain.model.Card;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CardMapper {

    public CardResponse toResponse(Card card) {
        if (card == null) return null;

        return CardResponse.builder()
                .id(card.getId())
                .cardNumber(card.getCardNumber())
                .expirationDate(card.getExpirationDate())
                .cvv(card.getCvv())
                .cardType(card.getCardType())
                .cardNetwork(card.getCardNetwork())
                .status(card.getStatus())
                .limit(card.getLimit())
                .createdAt(card.getCreatedAt())
                .updatedAt(card.getUpdatedAt())
                .accountId(card.getAccount().getId())
                .build();
    }

    public List<CardResponse> toResponseList(List<Card> cards) {
        return cards.stream()
                .map(this::toResponse)
                .toList();
    }
}
