package com.lumina_bank.paymentservice.application.mapper;

import com.lumina_bank.common.enums.payment.Currency;
import com.lumina_bank.paymentservice.api.response.TransactionHistoryItemResponse;
import com.lumina_bank.paymentservice.domain.enums.PaymentDirection;
import com.lumina_bank.paymentservice.domain.model.Payment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionHistoryMapper {

    public TransactionHistoryItemResponse toHistoryItem(
            Payment p,
            Long userId,
            Long accountId
    ) {
        if (accountId != null) {
            return mapAccountLevel(p, accountId);
        }

        return mapUserLevel(p, userId);
    }

    private TransactionHistoryItemResponse mapAccountLevel(
            Payment p,
            Long accountId
    ) {

        boolean isOutgoing =
                p.getFromAccountId() != null &&
                        p.getFromAccountId().equals(accountId);

        return TransactionHistoryItemResponse.builder()
                .paymentId(p.getId())
                .amount(resolveAmount(p, isOutgoing))
                .currency(resolveCurrency(p, isOutgoing))
                .date(p.getCreatedAt().toLocalDate())
                .direction(isOutgoing
                        ? PaymentDirection.OUTGOING
                        : PaymentDirection.INCOMING)
                .status(p.getPaymentStatus())
                .description(p.getDescription())
                .build();
    }


    private TransactionHistoryItemResponse mapUserLevel(
            Payment p,
            Long userId
    ) {

        boolean isSender = p.getUserId().equals(userId);
        boolean isReceiver =
                p.getToAccountOwnerId() != null &&
                        p.getToAccountOwnerId().equals(userId);

        boolean isInternal = isSender && isReceiver;

        if (isInternal) {
            return mapInternal(p);
        }

        return TransactionHistoryItemResponse.builder()
                .paymentId(p.getId())
                .amount(resolveAmount(p, isSender))
                .currency(resolveCurrency(p, isSender))
                .date(p.getCreatedAt().toLocalDate())
                .direction(isSender
                        ? PaymentDirection.OUTGOING
                        : PaymentDirection.INCOMING)
                .status(p.getPaymentStatus())
                .description(p.getDescription())
                .build();
    }

    private TransactionHistoryItemResponse mapInternal(Payment p) {

        return TransactionHistoryItemResponse.builder()
                .paymentId(p.getId())
                .outgoingAmount(p.getAmount())
                .outgoingCurrency(p.getFromCurrency())
                .incomingAmount(resolveIncomingAmount(p))
                .incomingCurrency(resolveIncomingCurrency(p))
                .date(p.getCreatedAt().toLocalDate())
                .direction(PaymentDirection.INTERNAL)
                .status(p.getPaymentStatus())
                .description(p.getDescription())
                .build();
    }

    private BigDecimal resolveAmount(Payment p, boolean isOutgoing) {
        if (isOutgoing) {
            return p.getAmount();
        }
        return resolveIncomingAmount(p);
    }

    private Currency resolveCurrency(Payment p, boolean isOutgoing) {
        if (isOutgoing) {
            return p.getFromCurrency();
        }
        return resolveIncomingCurrency(p);
    }

    private BigDecimal resolveIncomingAmount(Payment p) {
        return p.getConvertedAmount() != null
                ? p.getConvertedAmount()
                : p.getAmount();
    }

    private Currency resolveIncomingCurrency(Payment p) {
        return p.getToCurrency() != null
                ? p.getToCurrency()
                : p.getFromCurrency();
    }

}
