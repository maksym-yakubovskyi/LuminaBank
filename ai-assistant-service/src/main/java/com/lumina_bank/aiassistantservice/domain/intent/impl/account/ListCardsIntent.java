package com.lumina_bank.aiassistantservice.domain.intent.impl.account;

import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.CardResponse;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.result.data.account.CardsListData;
import com.lumina_bank.aiassistantservice.service.client.account.FeignAccountGateway;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ListCardsIntent implements IntentDefinition {
    private final FeignAccountGateway accountGateway;

    @Override
    public Intent intent() {
        return Intent.LIST_CARDS;
    }

    @Override
    public List<RequiredParam> requiredParams() {
        return List.of();
    }

    @Override
    public AssistantExecutionResult execute(Map<String, Object> params) {
        try {
            List<CardResponse> cards = accountGateway.getMyCards();

            if(cards.isEmpty()) {
                return AssistantExecutionResult.needConfirmation(
                        intent(),
                        "У вас ще немає карт. Хочете створити нову картку?",
                        Intent.CREATE_CARD
                );
            }

            return AssistantExecutionResult.success(
                    intent(),
                    new CardsListData(cards)
            );

        } catch (FeignException e) {
            return AssistantExecutionResult.error(
                    intent(),
                    "Не вдалося отримати картки");
        }
    }
}
