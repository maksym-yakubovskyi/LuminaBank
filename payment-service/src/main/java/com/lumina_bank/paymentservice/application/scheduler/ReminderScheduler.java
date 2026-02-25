package com.lumina_bank.paymentservice.application.scheduler;

import com.lumina_bank.common.dto.event.payment_events.PaymentReminderEvent;
import com.lumina_bank.paymentservice.application.service.PaymentTemplateService;
import com.lumina_bank.paymentservice.domain.model.PaymentTemplate;
import com.lumina_bank.paymentservice.infrastructure.messaging.producer.PaymentEventsPublisher;
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
public class ReminderScheduler {

    private static final Duration REMINDER_BEFORE = Duration.ofHours(1);

    private final PaymentTemplateService templateService;
    private final PaymentEventsPublisher eventsPublisher;

    @Scheduled(cron = "0 */5 * * * *")
    public void sendReminders() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderTime = now.plus(REMINDER_BEFORE);

        List<PaymentTemplate> templates =
                templateService.getTemplatesForReminder(now, reminderTime,REMINDER_BEFORE);

        log.info("Found {} templates for reminder", templates.size());

        for (PaymentTemplate template : templates) {

            try {

                log.info("Sending reminder for template {}", template.getId());

                eventsPublisher.publishPaymentReminder(
                        new PaymentReminderEvent(
                                template.getUserId(),
                                template.getName(),
                                template.getAmount(),
                                template.getCategory(),
                                template.getNextExecutionTime()
                        )
                );

                templateService.markReminderSent(template, now);

            } catch (Exception e) {

                log.error("Failed to send reminder for template {}",
                        template.getId(), e);
            }
        }
    }
}
