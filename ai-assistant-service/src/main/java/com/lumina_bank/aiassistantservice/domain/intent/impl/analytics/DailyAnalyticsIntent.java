package com.lumina_bank.aiassistantservice.domain.intent.impl.analytics;

import com.lumina_bank.aiassistantservice.domain.dto.client.analytics.AnalyticsDailyOverviewResponse;
import com.lumina_bank.aiassistantservice.domain.exception.ServiceCallException;
import com.lumina_bank.aiassistantservice.domain.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.domain.result.data.ConfirmationData;
import com.lumina_bank.aiassistantservice.domain.result.data.analytics.DailyAnalyticsData;
import com.lumina_bank.aiassistantservice.service.client.account.FeignAccountGateway;
import com.lumina_bank.aiassistantservice.service.client.analytics.FeignAnalyticsGateway;
import com.lumina_bank.aiassistantservice.util.ParseDateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.dto.client.account.AccountResponse;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.enums.ParamType;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyAnalyticsIntent implements IntentDefinition {

    private final FeignAnalyticsGateway analyticsGateway;
    private final FeignAccountGateway accountGateway;

    @Override
    public Intent intent() {
        return Intent.ANALYTICS_DAILY;
    }

    @Override
    public List<RequiredParam> requiredParams() {
        return List.of(
                new RequiredParam(
                        "accountId",
                        ParamType.NUMBER,
                        List.of(),
                        "ID of the account"),
                new RequiredParam(
                        "date",
                        ParamType.DATE,
                        List.of(),
                        "Date for analytics")
        );
    }

    @Override
    public AssistantExecutionResult execute(Map<String, Object> params) {
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

            long accountId;

            try {
                accountId = Long.parseLong(params.get("accountId").toString());
            } catch (NumberFormatException e) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("INVALID_ACCOUNT_ID")
                );
            }

            LocalDate date;

            if (!params.containsKey("date")) {
                date = LocalDate.now();
            } else {
                Optional<LocalDate> parsed =
                        ParseDateUtil.parseDate(
                                params.get("date")
                        );

                if (parsed.isEmpty()) {
                    return AssistantExecutionResult.needClarification(
                            intent(),
                            new ClarificationData("INVALID_DATE_FORMAT")
                    );
                }

                date = parsed.get();
            }

            AnalyticsDailyOverviewResponse overview =
                    analyticsGateway.getDailyOverview(accountId, date);

            return AssistantExecutionResult.success(
                    intent(),
                    new DailyAnalyticsData(
                            overview.totalIncome(),
                            overview.totalExpense(),
                            overview.transactionCount()
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
