package com.lumina_bank.paymentservice.service.payment;

import com.lumina_bank.paymentservice.dto.PaymentTemplateRequest;
import com.lumina_bank.paymentservice.dto.PaymentTemplateResponse;
import com.lumina_bank.paymentservice.enums.PaymentTemplateType;
import com.lumina_bank.paymentservice.enums.RecurrenceType;
import com.lumina_bank.paymentservice.exception.InvalidPaymentRequestException;
import com.lumina_bank.paymentservice.exception.PaymentTemplateNotFoundException;
import com.lumina_bank.paymentservice.model.PaymentTemplate;
import com.lumina_bank.paymentservice.repository.PaymentTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentTemplateService {

    private final PaymentTemplateRepository paymentTemplateRepository;
    private final PaymentService paymentService;

    @Transactional
    public PaymentTemplate createPaymentTemplate(PaymentTemplateRequest request, Long userId) {
        validateByType(request);
        String cron = buildCron(request);

        LocalDateTime nextExecutionTime = null;
        if (cron != null) {
            CronExpression cronExpression = CronExpression.parse(cron);
            nextExecutionTime = cronExpression.next(LocalDateTime.now());
        }

        String toCardNumber;
        String finalDescription;
        String category;
        if (request.type() == PaymentTemplateType.TRANSFER) {
            toCardNumber = request.toCardNumber();
            finalDescription = request.description();
            category ="TRANSFER";
        } else {
            toCardNumber = paymentService.getProviderCardNumber(request.providerId());
            finalDescription = paymentService.buildServiceDescription(
                    request.description(),
                    request.payerReference()
            );
            category = request.category();
        }

        PaymentTemplate template = PaymentTemplate.builder()
                .userId(userId)
                .name(request.name())
                .description(finalDescription)
                .fromCardNumber(request.fromCardNumber())
                .toCardNumber(toCardNumber)
                .type(request.type())
                .category(category)
                .amount(request.amount())
                .isRecurring(request.recurrenceType() != RecurrenceType.NONE)
                .recurrenceCron(cron)
                .nextExecutionTime(nextExecutionTime)
                .isActive(Boolean.TRUE)
                .build();

        return paymentTemplateRepository.save(template);
    }

    @Transactional
    public void deletePaymentTemplate(Long id) {
        PaymentTemplate paymentTemplate = getPaymentTemplateById(id);
        paymentTemplate.setIsActive(Boolean.FALSE);
        paymentTemplateRepository.save(paymentTemplate);
    }

    @Transactional(readOnly = true)
    public List<PaymentTemplateResponse> getTemplatesByUserId(Long userId) {
        return paymentTemplateRepository.findAllByUserIdAndIsActiveTrue(userId)
                .stream().map(PaymentTemplateResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public PaymentTemplate getPaymentTemplateById(Long paymentTemplateId) {
        return paymentTemplateRepository.findByIdAndIsActiveTrue(paymentTemplateId)
                .orElseThrow(() -> new PaymentTemplateNotFoundException("Payment template with id " + paymentTemplateId + " not found"));
    }

    public void updateNextExecutionTime(PaymentTemplate paymentTemplate) {
        if (!Boolean.TRUE.equals(paymentTemplate.getIsRecurring()) || paymentTemplate.getRecurrenceCron() == null)
            return;

        CronExpression cronExpression = CronExpression.parse(paymentTemplate.getRecurrenceCron());
        paymentTemplate.setNextExecutionTime(cronExpression.next(LocalDateTime.now()));
    }

    @Transactional(readOnly = true)
    public List<PaymentTemplate> getRecurredTemplatesBefore(LocalDateTime now) {
        return paymentTemplateRepository.findByIsRecurringTrueAndIsActiveTrueAndNextExecutionTimeBefore(now);
    }

    @Transactional
    public void savePaymentTemplate(PaymentTemplate paymentTemplate) {
        paymentTemplateRepository.save(paymentTemplate);
    }

    private String buildCron(PaymentTemplateRequest r) {
        if (r.recurrenceType() == RecurrenceType.NONE) return null;

        int hour = r.hour() == null ? 10 : r.hour();
        int minute = r.minute() == null ? 0 : r.minute();

        return switch (r.recurrenceType()) {
            case DAILY -> String.format("0 %d %d * * *", minute, hour);

            case WEEKLY -> {
                if (r.dayOfWeek() == null)
                    throw new InvalidPaymentRequestException("dayOfWeek is required for WEEKLY recurrence");

                String dow = r.dayOfWeek().name();
                yield String.format("0 %d %d * * %s", minute, hour, dow);
            }

            case MONTHLY -> {
                int dom = (r.dayOfMonth() == null) ? 1 : r.dayOfMonth();
                if (dom < 1 || dom > 28)
                    throw new InvalidPaymentRequestException("dayOfMonth must be 1..28");

                yield String.format("0 %d %d %d * *", minute, hour, dom);
            }

            default -> throw new InvalidPaymentRequestException("Unsupported recurrence type");
        };
    }

    private void validateByType(PaymentTemplateRequest r) {
        if (r.type() == PaymentTemplateType.TRANSFER) {
            if (r.toCardNumber() == null || r.toCardNumber().isBlank())
                throw new InvalidPaymentRequestException("toCardNumber is required for TRANSFER");

            if (r.fromCardNumber().equals(r.toCardNumber()))
                throw new InvalidPaymentRequestException("Cards must not be the same");
        }

        if (r.type() == PaymentTemplateType.SERVICE) {
            if (r.providerId() == null)
                throw new InvalidPaymentRequestException("providerId is required for SERVICE");

            if (r.payerReference() == null || r.payerReference().isBlank())
                throw new InvalidPaymentRequestException("payerReference is required for SERVICE");
        }
    }
}