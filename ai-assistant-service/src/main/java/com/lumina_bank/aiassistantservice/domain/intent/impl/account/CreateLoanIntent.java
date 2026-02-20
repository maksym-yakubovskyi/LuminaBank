package com.lumina_bank.aiassistantservice.domain.intent.impl.account;

import com.lumina_bank.aiassistantservice.domain.dto.AssistantContext;
import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.AccountResponse;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.LoanApplicationRequest;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.LoanOfferResponse;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.LoanResponse;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.enums.ParamType;
import com.lumina_bank.aiassistantservice.domain.exception.ServiceCallException;
import com.lumina_bank.aiassistantservice.domain.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.domain.result.data.ConfirmationData;
import com.lumina_bank.aiassistantservice.domain.result.data.account.LoanConfirmationData;
import com.lumina_bank.aiassistantservice.domain.result.data.account.LoanCreatedData;
import com.lumina_bank.aiassistantservice.service.client.account.FeignAccountGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateLoanIntent implements IntentDefinition {
    private final FeignAccountGateway accountGateway;

    @Override
    public Intent intent() {
        return Intent.CREATE_LOAN;
    }

    @Override
    public boolean requiresFinalConfirmation() {
        return true;
    }

    @Override
    public List<RequiredParam> requiredParams(AssistantContext context) {
        return List.of(
                new RequiredParam(
                        "accountId",
                        ParamType.NUMBER,
                        List.of(),
                        "ID of the credit account to create loan"
                ),
                new RequiredParam(
                        "amount",
                        ParamType.NUMBER,
                        List.of(),
                        "Requested loan amount"
                ),
                new RequiredParam(
                        "offerIndex",
                        ParamType.NUMBER,
                        List.of(),
                        "Selected loan offer number"
                )
        );
    }

    @Override
    public AssistantExecutionResult execute(Map<String, Object> params, UUID conversationId, AssistantContext context) {
        try{
            List<AccountResponse> accounts = accountGateway.getAvailableCreditAccounts();

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

            if (!params.containsKey("amount")) {
                return AssistantExecutionResult.askParam(
                        intent(),
                        requiredParams(context).get(1)
                );
            }

            BigDecimal amount;

            try {
                amount = new BigDecimal(
                        params.get("amount").toString()
                );
            } catch (NumberFormatException e) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("INVALID_AMOUNT_FORMAT")
                );
            }

            if (amount.signum() < 0) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("AMOUNT_NEGATIVE")
                );
            }

            List<LoanOfferResponse> offers = accountGateway.getLoanOffers(
                    new LoanApplicationRequest(
                            accountId,
                            amount,
                            null
                    )
            );

            if (offers.isEmpty()) {
                return AssistantExecutionResult.error(
                        intent(),
                        "NO_OFFERS"
                );
            }

            if (!params.containsKey("offerIndex")) {

                return AssistantExecutionResult.askParam(
                        intent(),
                        new RequiredParam(
                                "offerIndex",
                                ParamType.NUMBER,
                                IntStream.range(0, offers.size())
                                        .mapToObj(i ->
                                                (i + 1) + " | "
                                                        + offers.get(i).termMonths()
                                                        + " months | "
                                                        + offers.get(i).interestRate()
                                                        + "% | "
                                                        + offers.get(i).monthlyPayment()
                                                        + " per month"
                                        )
                                        .toList(),
                                "Select loan offer"
                        )
                );
            }

            int index;
            try {
                index = Integer.parseInt(params.get("offerIndex").toString()) - 1;
            } catch (Exception e) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("INVALID_OFFER_SELECTION")
                );
            }

            if (index < 0 || index >= offers.size()) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("OFFER_OUT_OF_RANGE")
                );
            }

            LoanOfferResponse selected = offers.get(index);

            // Зберігаємо потрібні параметри для perform()
            params.put("termMonths", selected.termMonths());

            return AssistantExecutionResult.confirmFinal(
                    intent(),
                    new LoanConfirmationData(
                            accountId,
                            amount,
                            selected
                    )
            );
        }catch (ServiceCallException e) {
            return AssistantExecutionResult.error(
                    intent(),
                    e.getMessage());
        }
    }

    @Override
    public AssistantExecutionResult perform(Map<String, Object> params, AssistantContext context) {
        try {

            LoanApplicationRequest request =
                    new LoanApplicationRequest(
                            Long.parseLong(params.get("accountId").toString()),
                            new BigDecimal(params.get("amount").toString()),
                            Integer.parseInt(params.get("termMonths").toString())
                    );

            LoanResponse loan =
                    accountGateway.approveLoan(request);

            return AssistantExecutionResult.success(
                    intent(),
                    new LoanCreatedData(loan)
            );

        } catch (ServiceCallException e) {
            return AssistantExecutionResult.error(intent(), e.getMessage());
        }
    }
}
