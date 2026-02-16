package com.lumina_bank.aiassistantservice.domain.intent.impl.account;

import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.AccountResponse;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.CardCreateDto;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.CardResponse;
import com.lumina_bank.aiassistantservice.domain.result.data.ConfirmationData;
import com.lumina_bank.aiassistantservice.domain.result.data.account.CardCreatedData;
import com.lumina_bank.aiassistantservice.domain.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.domain.result.data.EmptyData;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.enums.ParamType;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.service.client.account.FeignAccountGateway;
import com.lumina_bank.aiassistantservice.util.ParseEnumUtil;
import com.lumina_bank.common.enums.account.CardNetwork;
import com.lumina_bank.common.enums.account.CardType;
import com.lumina_bank.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateCardIntent implements IntentDefinition{
    private final FeignAccountGateway accountGateway;

    @Override
    public Intent intent() {
        return Intent.CREATE_CARD;
    }

    @Override
    public List<RequiredParam> requiredParams() {
        return List.of(
                new RequiredParam(
                        "accountId",
                        ParamType.NUMBER,
                        List.of(),
                        "ID of the account to issue the card for."
                ),
                new RequiredParam(
                        "cardType",
                        ParamType.ENUM,
                        ParseEnumUtil.enumValues(CardType.class),
                        "Type of card. "
                ),
                new RequiredParam(
                        "cardNetwork",
                        ParamType.ENUM,
                        ParseEnumUtil.enumValues(CardNetwork.class),
                        "Payment network."
                ),
                new RequiredParam(
                        "limit",
                        ParamType.NUMBER,
                        List.of(),
                        "Card limit amount. Must be a positive number."
                )
        );
    }

    @Override
    public boolean requiresFinalConfirmation() {
        return true;
    }

    @Override
    public AssistantExecutionResult execute(
            Map<String, Object> params
    ) {
        try {
            List<AccountResponse> accounts = accountGateway.getUserAccounts();

            if (accounts.isEmpty()) {
                return AssistantExecutionResult.confirmNavigation(
                        intent(),
                        new ConfirmationData(
                                "NO_ACCOUNTS",
                                Map.of("nextIntent",Intent.CREATE_ACCOUNT)),
                        Intent.CREATE_ACCOUNT
                );
            }

            if (!params.containsKey("accountId")) {

                if (accounts.size() == 1) {
                    params.put("accountId", accounts.getFirst().id());
                } else {
                    return AssistantExecutionResult.askParam(
                            intent(),
                            new RequiredParam(
                                    "accountId",
                                    ParamType.NUMBER,
                                    accounts.stream()
                                            .map(a -> a.id() + " | " + a.currency() + " | " + a.iban())
                                            .toList(),
                                    "Accounts list to select"
                            )
                    );
                }
            }

            try {
                Long.parseLong(params.get("accountId").toString());
            } catch (NumberFormatException e) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("INVALID_ACCOUNT_ID")
                );
            }

            if (!params.containsKey("cardType")) {
                return AssistantExecutionResult.askParam(
                        intent(),
                        requiredParams().get(1)
                );
            }

            Optional<CardType> cardType =
                    ParseEnumUtil.parseEnumSafe(
                            CardType.class,
                            params.get("cardType")
                    );

            if (cardType.isEmpty()) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("INVALID_CARD_TYPE")
                );
            }

            if (!params.containsKey("cardNetwork")) {
                return AssistantExecutionResult.askParam(
                        intent(),
                        requiredParams().get(2)
                );
            }

            Optional<CardNetwork> cardNetwork =
                    ParseEnumUtil.parseEnumSafe(
                            CardNetwork.class,
                            params.get("cardNetwork")
                    );

            if (cardNetwork.isEmpty()) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("INVALID_CARD_NETWORK")
                );
            }

            if (!params.containsKey("limit")) {
                return AssistantExecutionResult.askParam(
                        intent(),
                        requiredParams().get(3)
                );
            }

            BigDecimal limit;

            try {
                limit = new BigDecimal(
                        params.get("limit").toString()
                );
            } catch (NumberFormatException e) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("INVALID_LIMIT_FORMAT")
                );
            }

            if (limit.signum() < 0) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("LIMIT_NEGATIVE")
                );
            }

            return AssistantExecutionResult.success(
                    intent(),
                    new EmptyData()
            );
        }catch (BusinessException e) {
            return AssistantExecutionResult.error(
                    intent(),
                    e.getMessage());
        }
    }

    @Override
    public AssistantExecutionResult perform(Map<String, Object> params) {
        try {
            Long accountId = Long.parseLong(params.get("accountId").toString());
            CardType cardType = CardType.valueOf(params.get("cardType").toString());
            CardNetwork cardNetwork = CardNetwork.valueOf(params.get("cardNetwork").toString());
            BigDecimal limit = new BigDecimal(params.get("limit").toString());

            CardResponse created =
                    accountGateway.createCard(
                            accountId,
                            new CardCreateDto(
                                    cardType.name(),
                                    cardNetwork.name(),
                                    limit
                            )
                    );

            return AssistantExecutionResult.success(
                    intent(),
                    new CardCreatedData(created)
            );
        }catch (BusinessException e) {
            return AssistantExecutionResult.error(
                    intent(),
                    e.getMessage());
        }
    }
}
