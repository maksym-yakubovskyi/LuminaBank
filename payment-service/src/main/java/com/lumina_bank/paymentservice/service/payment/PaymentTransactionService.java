package com.lumina_bank.paymentservice.service.payment;

import com.lumina_bank.common.dto.event.payment_events.PaymentBlockedEvent;
import com.lumina_bank.common.dto.event.payment_events.PaymentCompletedEvent;
import com.lumina_bank.common.dto.event.payment_events.PaymentFlaggedEvent;
import com.lumina_bank.paymentservice.enums.PaymentStatus;
import com.lumina_bank.paymentservice.model.Payment;
import com.lumina_bank.paymentservice.model.PaymentTemplate;
import com.lumina_bank.paymentservice.repository.PaymentRepository;
import com.lumina_bank.paymentservice.service.event.PaymentEventsPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentTransactionService {
    private final PaymentRepository paymentRepository;
    private final PaymentEventsPublisher paymentEventsPublisher;

    @Transactional
    public void markProcessing(Payment payment) {
        if (!payment.getPaymentStatus().equals(PaymentStatus.PENDING)) return;
        payment.setPaymentStatus(PaymentStatus.PROCESSING);
        paymentRepository.save(payment);
    }

    @Transactional
    public void markPending(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        if (payment == null) return;
        if (!payment.getPaymentStatus().equals(PaymentStatus.RISK_PENDING)) return;
        payment.setPaymentStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);
    }

    @Transactional
    public void markSuccess(Payment  payment) {
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setCompletedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        paymentEventsPublisher.publishPaymentCompleted(
                new PaymentCompletedEvent(
                        payment.getId(),
                        payment.getUserId(),
                        payment.getToAccountOwnerId(),
                        payment.getFromAccountId(),
                        payment.getToAccountId(),
                        payment.getAmount(),
                        payment.getConvertedAmount(),
                        payment.getFromCurrency().name(),
                        payment.getToCurrency().name(),
                        payment.getCategory(),
                        Instant.now()
                )
        );
    }

    @Transactional
    public void markBlocking(Long paymentId, int riskScore) {
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        if (payment == null) return;
        if (!payment.getPaymentStatus().equals(PaymentStatus.RISK_PENDING)) return;
        payment.setPaymentStatus(PaymentStatus.BLOCKED);
        paymentRepository.save(payment);

        paymentEventsPublisher.publishPaymentBlocking(
                new PaymentBlockedEvent(
                        payment.getId(),
                        payment.getUserId(),
                        payment.getAmount(),
                        payment.getCategory(),
                        riskScore,
                        Instant.now()
                )
        );
    }

    @Transactional
    public void markFlagged(Long paymentId,int riskScore) {
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        if (payment == null) return;
        if (!payment.getPaymentStatus().equals(PaymentStatus.RISK_PENDING)) return;
        payment.setPaymentStatus(PaymentStatus.FLAGGED);
        paymentRepository.save(payment);

        paymentEventsPublisher.publishPaymentFlagged(
                new PaymentFlaggedEvent(
                        payment.getId(),
                        payment.getUserId(),
                        payment.getAmount(),
                        payment.getCategory(),
                        riskScore,
                        Instant.now()
                )
        );
    }

    @Transactional
    public void markFailed(Payment  payment) {
        payment.setPaymentStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
    }

    @Transactional
    public Payment createPendingPayment(PaymentTemplate paymentTemplate) {
        Payment payment = Payment.builder()
                .userId(paymentTemplate.getUserId())
                .fromCardNumber(paymentTemplate.getFromCardNumber())
                .toCardNumber(paymentTemplate.getToCardNumber())
                .amount(paymentTemplate.getAmount())
                .description(paymentTemplate.getDescription())
                .template(paymentTemplate)
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        return paymentRepository.save(payment);
    }

}