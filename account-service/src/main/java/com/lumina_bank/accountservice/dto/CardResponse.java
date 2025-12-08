package com.lumina_bank.accountservice.dto;

import com.lumina_bank.accountservice.model.Card;
import jakarta.persistence.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Builder
public record CardResponse(
        Long id,
        String cardNumber,
        YearMonth expirationDate,
        String cvv,
        String cardType,
        String cardNetwork,
        String accountType,
        String status,
        BigDecimal limit,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CardResponse fromEntity(Card card) {
        return CardResponse.builder().
                id(card.getId()).
                cardNumber(card.getCardNumber()).
                expirationDate(card.getExpirationDate()).
                cvv(card.getCvv()).
                cardType(card.getCardType().name()).
                cardNetwork(card.getCardNetwork().name()).
                status(card.getStatus().name()).
                limit(card.getLimit()).
                createdAt(card.getCreatedAt()).
                updatedAt(card.getUpdatedAt()).
                build();
    }
}
