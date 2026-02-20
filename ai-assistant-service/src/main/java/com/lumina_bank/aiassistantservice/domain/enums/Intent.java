package com.lumina_bank.aiassistantservice.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum Intent {
    CHECK_BALANCE("User wants to see the current balance of their account(s)."),

    CREATE_ACCOUNT("User wants to open a new bank account."),
    CREATE_CARD("User wants to issue a new card for one of their existing accounts."),

    LIST_ACCOUNTS("User wants to see list of accounts or information about an account/accounts."),
    LIST_CARDS("User wants to see all cards or information about an card/cards."),
    LIST_ACCOUNT_CARDS("User wants to see cards that belong to a specific account."),

    LIST_LOANS("User wants to see all their loans or details about existing loans."),
    CREATE_LOAN("User wants to apply for a new loan."),

    USER_INFORMATION("User wants to view their personal profile or information."),
    UPDATE_USER_INFORMATION("User wants to update or change their personal profile information."),

    PAYMENT_HISTORY("User wants to see transaction history for a specific account (not analytics)."),
    TRANSFER_BY_CARD("User wants to transfer money from their card to another card."),
    PAYMENT_SERVICE_PROVIDER(" User wants to pay a registered service provider (utilities, mobile, internet, etc.)."),
    LIST_PAYMENT_TEMPLATES("User wants to view their saved payment templates."),
    PAYMENT_BY_TEMPLATE("User wants to make a payment using an existing saved template."),

    ANALYTICS_MONTHLY("User wants a monthly summary of income, expenses, cash flow."),
    ANALYTICS_DAILY("User wants a daily summary of income, expenses."),
    ANALYTICS_BY_CATEGORY("User wants to see expenses grouped by spending categories."),
    ANALYTICS_TOP_RECIPIENTS("User wants to see the most frequent or highest-value payment recipients."),
    ANALYTICS_FORECAST("User wants a prediction of future expenses, income, cash flow, financial trends, or upcoming financial outlook based on past activity."),
    ANALYTICS_RECOMMENDATIONS("User wants personalized financial advice, spending improvement suggestions, budgeting tips, or money management insights based on their financial activity."),

    KNOWLEDGE_QUERY("User asks for financial education, banking rules, policies, app functionality, or general explanations not related to executing an operation."),

    UNKNOWN("The message does not match any known intent or is too unclear to classify.");

    private final String description;

    public static String buildIntentListForPrompt() {
        return Arrays.stream(values())
                .filter(i -> i != UNKNOWN)
                .map(i -> "- " + i.name() + " → " + i.description)
                .collect(Collectors.joining("\n"));
    }

    public static String buildIntentListForPromptWithout(Intent... excluded) {
        Set<Intent> excludedSet = new HashSet<>();
        if (excluded != null) {
            excludedSet.addAll(Arrays.asList(excluded));
        }
        return Arrays.stream(values())
                .filter(intent -> !excludedSet.contains(intent))
                .map(intent -> "- " + intent.name() + " → " + intent.getDescription())
                .collect(Collectors.joining("\n"));
    }

}
