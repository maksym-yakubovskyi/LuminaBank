package com.lumina_bank.paymentservice.application.scheduler;

import com.lumina_bank.common.exception.BusinessException;
import com.lumina_bank.paymentservice.application.service.PaymentExecutionService;
import com.lumina_bank.paymentservice.application.service.PaymentTransactionService;
import com.lumina_bank.paymentservice.domain.model.Payment;
import com.lumina_bank.paymentservice.domain.model.PaymentTemplate;
import com.lumina_bank.paymentservice.application.service.PaymentTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecurringPaymentScheduler {

    private final PaymentExecutionService executionService;
    private final PaymentTransactionService stateService;
    private final PaymentTemplateService paymentTemplateService;

    @Scheduled(cron = "0 0 * * * *")
    public void executeRecurringPayments() {
        log.info("Starting recurring payment execution at {}", LocalDateTime.now());

        List<PaymentTemplate> templates = paymentTemplateService.getRecurredTemplatesBefore(LocalDateTime.now());
        log.info("Found {} templates ready for recurring payment execution", templates.size());

        for (PaymentTemplate template : templates) {
            log.debug("Processing recurring payment template id={}", template.getId());

            try {
                Payment payment = stateService.createPendingPayment(template);
                executionService.execute(payment);

                log.debug("Successfully executed recurring payment for template id={}", template.getId());

                paymentTemplateService.updateNextExecutionTime(template);
                paymentTemplateService.savePaymentTemplate(template);

                log.debug("Updated next execution time for template id={} to {}",
                        template.getId(), template.getNextExecutionTime());

            } catch (BusinessException e) {
                log.warn("Business exception during recurring payment for template id={}: {}",
                        template.getId(), e.getMessage());
            } catch (Exception e) {
                log.error("Unexpected error during recurring payment for template id={}: {}",
                        template.getId(), e.getMessage(), e);
            }
        }
        log.info("Recurring payment scheduler completed at {}", LocalDateTime.now());
    }
}