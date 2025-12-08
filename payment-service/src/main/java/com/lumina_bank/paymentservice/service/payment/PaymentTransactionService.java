package com.lumina_bank.paymentservice.service.payment;

import com.lumina_bank.paymentservice.enums.PaymentStatus;
import com.lumina_bank.paymentservice.model.Payment;
import com.lumina_bank.paymentservice.model.PaymentTemplate;
import com.lumina_bank.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

//TODO: додати логування
@Service
@RequiredArgsConstructor
public class PaymentTransactionService {
    private final PaymentRepository paymentRepository;

    @Transactional
    public void markProcessing(Payment payment) {
        if (!payment.getPaymentStatus().equals(PaymentStatus.PENDING)) return;
        payment.setPaymentStatus(PaymentStatus.PROCESSING);
        paymentRepository.save(payment);
    }

    @Transactional
    public void updatePaymentStatus(Payment payment, PaymentStatus paymentStatus) {
        payment.setPaymentStatus(paymentStatus);
        payment.setCompletedAt(LocalDateTime.now());
        paymentRepository.save(payment);
    }

    @Transactional
    public Payment createPendingPayment(PaymentTemplate paymentTemplate) {
        Payment payment = Payment.builder()
                .userId(paymentTemplate.getUserId())
                .fromAccountId(paymentTemplate.getFromAccountId())
                .toAccountId(paymentTemplate.getToAccountId())
                .amount(paymentTemplate.getAmount())
                .description(paymentTemplate.getDescription())
                .template(paymentTemplate)
                .paymentStatus(PaymentStatus.PENDING)
                .paymentType(paymentTemplate.getPaymentType())
                .build();

        return paymentRepository.save(payment);
    }
}
