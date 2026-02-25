package com.lumina_bank.notificationservice.application.email.template;

import com.lumina_bank.common.dto.event.payment_events.PaymentBlockedEvent;
import com.lumina_bank.common.dto.event.payment_events.PaymentCompletedEvent;
import com.lumina_bank.common.dto.event.payment_events.PaymentFlaggedEvent;
import com.lumina_bank.common.dto.event.payment_events.PaymentReminderEvent;
import com.lumina_bank.common.dto.event.user_events.BusinessUserRegisteredEvent;
import com.lumina_bank.common.dto.event.user_events.IndividualUserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailTemplateRenderer {
    private final SpringTemplateEngine templateEngine;

    public String renderVerification(String code) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process("email/verification", context);
    }

    public String renderLogin(String device, LocalDateTime loginTime){
        Context context = new Context();
        context.setVariable("device", device);
        context.setVariable("loginTime", formatTime(loginTime));
        return templateEngine.process("email/login", context);
    }

    public String renderWelcomeIndividual(IndividualUserRegisteredEvent event) {
        Context context = new Context();
        context.setVariable("firstName", event.firstName());
        context.setVariable("email", event.email());
        context.setVariable("registeredAt", formatTime(event.registeredAt()));
        return templateEngine.process("email/welcome-individual", context);
    }

    public String renderWelcomeBusiness(BusinessUserRegisteredEvent event) {
        Context context = new Context();
        context.setVariable("companyName", event.companyName());
        context.setVariable("adrpou", event.adrpou());
        context.setVariable("email", event.email());
        context.setVariable("category", event.category());
        context.setVariable("registeredAt", formatTime(event.registeredAt()));
        return templateEngine.process("email/welcome-business", context);
    }

    public String renderPaymentCompleted(PaymentCompletedEvent event) {
        Context context = new Context();

        context.setVariable("paymentId", formatPaymentId(event.paymentId()));
        context.setVariable("amount", formatAmount(event.amount()));
        context.setVariable("convertedAmount", formatAmountNullable(event.convertedAmount()));
        context.setVariable("fromCurrency", event.fromCurrency());
        context.setVariable("toCurrency", event.toCurrency());
        context.setVariable("category", event.category());
        context.setVariable("completedAt", formatTime(event.completedAt()));

        return templateEngine.process("email/payment-completed", context);
    }

    public String renderPaymentFlagged(PaymentFlaggedEvent event) {
        Context context = new Context();

        context.setVariable("paymentId", formatPaymentId(event.paymentId()));
        context.setVariable("amount", formatAmount(event.amount()));
        context.setVariable("category", event.category());
        context.setVariable("riskScore", event.riskScore());
        context.setVariable("flaggedAt", formatTime(event.flaggedAt()));

        return templateEngine.process("email/payment-flagged", context);
    }

    public String renderPaymentBlocked(PaymentBlockedEvent event) {
        Context context = new Context();

        context.setVariable("paymentId", formatPaymentId(event.paymentId()));
        context.setVariable("amount", formatAmount(event.amount()));
        context.setVariable("category", event.category());
        context.setVariable("riskScore", event.riskScore());
        context.setVariable("blockedAt", formatTime(event.blockedAt()));

        return templateEngine.process("email/payment-blocked", context);
    }

    public String renderPaymentReminder(PaymentReminderEvent event) {

        Context context = new Context();

        context.setVariable("templateName", event.templateName());
        context.setVariable("amount", formatAmount(event.amount()));
        context.setVariable("category", event.category());
        context.setVariable("executionTime", formatTime(event.executionTime()));

        return templateEngine.process("email/payment-reminder", context);
    }

    private String formatTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "-";
        }
        return dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    private String formatAmount(BigDecimal amount) {
        if (amount == null) return "-";
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String formatAmountNullable(BigDecimal amount) {
        if (amount == null) return null;
        return formatAmount(amount);
    }

    private String formatPaymentId(Long id) {
        if (id == null) return "-";
        return "PAY-" + String.format("%06d", id);
    }
}
