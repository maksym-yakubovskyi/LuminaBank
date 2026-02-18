package com.lumina_bank.aiassistantservice.domain.intent.impl.analytics;

import com.lumina_bank.aiassistantservice.domain.dto.AssistantContext;
import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.AccountResponse;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.enums.ParamType;
import com.lumina_bank.aiassistantservice.domain.exception.ServiceCallException;
import com.lumina_bank.aiassistantservice.domain.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.domain.result.data.ConfirmationData;
import com.lumina_bank.aiassistantservice.domain.result.data.analytics.MonthlyAnalyticsData;
import com.lumina_bank.aiassistantservice.service.client.account.FeignAccountGateway;
import com.lumina_bank.aiassistantservice.service.client.analytics.FeignAnalyticsGateway;
import com.lumina_bank.aiassistantservice.util.ParseDateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class MonthlyAnalyticsIntent implements IntentDefinition {
    private final FeignAnalyticsGateway analyticsGateway;
    private final FeignAccountGateway accountGateway;

    @Override
    public Intent intent() {
        return Intent.ANALYTICS_MONTHLY;
    }

    @Override
    public List<RequiredParam> requiredParams(AssistantContext context) {
        return List.of(
                new RequiredParam(
                        "accountId",
                        ParamType.NUMBER,
                        List.of(),
                        "ID of the account"),
                new RequiredParam(
                        "yearMonth",
                        ParamType.YEAR_MONTH,
                        List.of(),
                        "Month for analytics")
        );
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
                                Map.of("nextIntent", Intent.CREATE_ACCOUNT)
                        ),
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

            Long accountId;

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

            YearMonth yearMonth;

            if (!params.containsKey("yearMonth")) {
                yearMonth = YearMonth.now();
            } else {

                Optional<YearMonth> parsed =
                        ParseDateUtil.parseYearMonth(
                                params.get("yearMonth")
                        );

                if (parsed.isEmpty()) {
                    return AssistantExecutionResult.needClarification(
                            intent(),
                            new ClarificationData("INVALID_YEAR_MONTH_FORMAT")
                    );
                }

                yearMonth = parsed.get();
            }

            var summary =
                    analyticsGateway.getMonthlyOverview(accountId, yearMonth);

            return AssistantExecutionResult.success(
                    intent(),
                    new MonthlyAnalyticsData(
                            summary.totalIncome(),
                            summary.totalExpense(),
                            summary.cashFlow(),
                            summary.transactionCount()
                    )
            );

        } catch (ServiceCallException e) {
            return AssistantExecutionResult.error(
                    intent(),
                    e.getMessage()
            );
        }
    }
}
