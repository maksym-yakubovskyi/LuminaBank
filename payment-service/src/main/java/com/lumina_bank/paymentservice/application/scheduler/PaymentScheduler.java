package com.lumina_bank.paymentservice.application.scheduler;

import com.lumina_bank.common.exception.BusinessException;
import com.lumina_bank.paymentservice.application.service.PaymentExecutionService;
import com.lumina_bank.paymentservice.domain.enums.PaymentStatus;
import com.lumina_bank.paymentservice.domain.model.Payment;
import com.lumina_bank.paymentservice.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentScheduler {
    private final PaymentRepository paymentRepository;
    private final PaymentExecutionService executionService;
    private static final Duration CANCEL_WINDOW = Duration.ofSeconds(30);

    @Scheduled(fixedDelay = 5000)
    public void executePendingPayments() {
        List<Payment> payments = paymentRepository
                .findAllByPaymentStatusAndCreatedAtBefore(PaymentStatus.PENDING, LocalDateTime.now().minus(CANCEL_WINDOW));

        if (payments.isEmpty()) {
            return;
        }

        log.debug("Found {} pending payments to process", payments.size());

        for (Payment payment : payments) {
            try {
                executionService.execute(payment);
            } catch (BusinessException e) {
                log.warn("Business exception occurred during processing of payment with id={}: {}", payment.getId(), e.getMessage());
            } catch (Exception e) {
                log.warn("Unexpected error occurred during processing of payment with id={}: {}", payment.getId(), e.getMessage());
            }
        }
    }
}
