package com.lumina_bank.aiassistantservice.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum Intent {
    CHECK_BALANCE(
            "User wants to see the current available balance of one or more accounts.",
            "Check balance"
    ),

    CREATE_ACCOUNT(
            "User wants to open a new bank account.",
            "Open a new account"
    ),
    CREATE_CARD(
            "User wants to issue a new card for one of their existing accounts.",
            "Create a new card"
    ),

    LIST_ACCOUNTS(
            "User wants to see list of accounts or information about an account/accounts.",
            "View your accounts"
    ),
    LIST_CARDS(
            "User wants to see all cards or information about an card/cards.",
            "View all your cards"
    ),
    LIST_ACCOUNT_CARDS(
            "User wants to see cards that belong to a specific account.",
            "View cards for an account"
    ),

    LIST_LOANS(
            "User wants to see all their loans or details about existing loans.",
            "View your loans"
    ),
    CREATE_LOAN(
            "User wants to apply for a new loan.",
            "Apply for a loan"
    ),

    USER_INFORMATION(
            "User wants to view their personal profile or information.",
            "View their personal profile"
    ),
    UPDATE_USER_INFORMATION(
            "User wants to update or change their personal profile information.",
            "Update their personal profile information"
    ),

    PAYMENT_HISTORY(
            "User wants to see transaction history for a specific account (not analytics).",
            "View transaction history"
    ),
    TRANSFER_BY_CARD(
            "User wants to transfer money from their card to another card.",
            "Make a money transfer"
    ),
    PAYMENT_SERVICE_PROVIDER(
            " User wants to pay a registered service provider (utilities, mobile, internet, etc.).",
            "Pay a service provider"
    ),
    LIST_PAYMENT_TEMPLATES(
            "User wants to view their saved payment templates.",
            "View payment templates"
    ),
    PAYMENT_BY_TEMPLATE(
            "User wants to make a payment using an existing saved template.",
            "Make a payment using a template"
    ),

    ANALYTICS_MONTHLY(
            "User wants a monthly summary of income, expenses, cash flow.",
            "View monthly financial summary"
    ),
    ANALYTICS_DAILY(
            "User wants a daily summary of income, expenses.",
            "View daily financial summary"
    ),
    ANALYTICS_BY_CATEGORY(
            "User wants to see expenses grouped by spending categories.",
            "View expenses by category"
    ),
    ANALYTICS_TOP_RECIPIENTS(
            "User wants to see the most frequent or highest-value payment recipients.",
            "View most frequent recipients"
    ),
    ANALYTICS_FORECAST(
            "User wants a prediction of future expenses, income, cash flow, financial trends, or upcoming financial outlook based on past activity.",
            "View financial forecast"
    ),
    ANALYTICS_RECOMMENDATIONS(
            "User wants personalized financial advice, spending improvement suggestions, budgeting tips, or money management insights based on their financial activity.",
            "Get financial recommendations"
    ),

    KNOWLEDGE_QUERY(
            "User asks for explanations, information, or educational knowledge related to banking, finance, payments, accounts, cards, loans, interest rates, fees, banking rules, policies, or how the banking app works. The user is asking a question but NOT requesting an operation.",
            null
    ),
    ASSISTANT_INFO(
            "User asks about the assistant itself, its identity, capabilities, or role.",
            null
    ),
    OUT_OF_SCOPE(
            "Message is unrelated to banking, finances, payments, accounts, cards, loans, or the assistant’s banking functionality. Includes greetings, casual conversation, jokes, random words, or short messages that do not request banking information or actions.",
            null),

    UNKNOWN(
            "The message does not match any known intent or is too unclear to classify.",
            null);

    private final String description;
    private final String actionLabel;

    public static String buildIntentListForPrompt() {
        return Arrays.stream(values())
                .filter(i -> i != UNKNOWN)
                .map(i -> """
                    %s:
                    %s
                    """.formatted(i.name(), i.getDescription()))
                .collect(Collectors.joining("\n"));
    }

}
