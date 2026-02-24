package com.lumina_bank.aiassistantservice.domain.assistant.intent.impl.account;

import com.lumina_bank.aiassistantservice.domain.assistant.AssistantContext;
import com.lumina_bank.aiassistantservice.domain.assistant.RequiredParam;
import com.lumina_bank.aiassistantservice.infrastructure.external.account.dto.AccountResponse;
import com.lumina_bank.aiassistantservice.infrastructure.external.account.dto.CardCreateRequest;
import com.lumina_bank.aiassistantservice.infrastructure.external.account.dto.CardResponse;
import com.lumina_bank.aiassistantservice.domain.exception.ServiceCallException;
import com.lumina_bank.aiassistantservice.domain.assistant.result.data.ConfirmationData;
import com.lumina_bank.aiassistantservice.domain.assistant.result.data.account.CardCreatedData;
import com.lumina_bank.aiassistantservice.domain.assistant.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.domain.assistant.result.data.EmptyData;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.enums.ParamType;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.assistant.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.infrastructure.external.account.FeignAccountGateway;
import com.lumina_bank.aiassistantservice.domain.util.ParseEnumUtil;
import com.lumina_bank.aiassistantservice.infrastructure.external.account.enums.CardNetwork;
import com.lumina_bank.aiassistantservice.infrastructure.external.account.enums.CardType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
    public List<RequiredParam> requiredParams(AssistantContext context) {
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
            Map<String, Object> params,
            UUID conversationId,
            AssistantContext context
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

            long accountId;
            try {
                accountId = Long.parseLong(params.get("accountId").toString());
            } catch (NumberFormatException e) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("INVALID_ACCOUNT_ID")
                );
            }

            boolean exists = accounts.stream()
                    .anyMatch(a -> a.id().equals(accountId));

            if (!exists) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("ACCOUNT_NOT_FOUND")
                );
            }

            if (!params.containsKey("cardType")) {
                return AssistantExecutionResult.askParam(
                        intent(),
                        requiredParams(context).get(1)
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
                        requiredParams(context).get(2)
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
                        requiredParams(context).get(3)
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
        }catch (ServiceCallException e) {
            return AssistantExecutionResult.error(
                    intent(),
                    e.getMessage());
        }
    }

    @Override
    public AssistantExecutionResult perform(Map<String, Object> params,AssistantContext context) {
        try {
            Long accountId = Long.parseLong(params.get("accountId").toString());
            CardType cardType = CardType.valueOf(params.get("cardType").toString());
            CardNetwork cardNetwork = CardNetwork.valueOf(params.get("cardNetwork").toString());
            BigDecimal limit = new BigDecimal(params.get("limit").toString());

            CardResponse created =
                    accountGateway.createCard(
                            accountId,
                            new CardCreateRequest(
                                    cardType,
                                    cardNetwork,
                                    limit
                            )
                    );

            return AssistantExecutionResult.success(
                    intent(),
                    new CardCreatedData(created)
            );
        }catch (ServiceCallException e) {
            return AssistantExecutionResult.error(
                    intent(),
                    e.getMessage());
        }
    }
}
