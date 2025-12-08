package com.lumina_bank.paymentservice.service.payment;

import com.lumina_bank.paymentservice.dto.PaymentTemplateRequest;
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

//TODO: додати логування
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentTemplateService {

    private final PaymentTemplateRepository paymentTemplateRepository;

    @Transactional
    public PaymentTemplate createPaymentTemplate(PaymentTemplateRequest request) {
        validatePaymentTemplateRequest(request);

        PaymentTemplate paymentTemplate = PaymentTemplate.builder()
                .userId(request.userId())
                .name(request.name())
                .description(request.description())
                .fromAccountId(request.fromAccountId())
                .toAccountId(request.toAccountId())
                .amount(request.amount())
                .isRecurring(request.isRecurring())
                .recurrenceCron(request.recurrenceCron())
                .isActive(Boolean.TRUE)
                .paymentType(request.paymentType())
                .build();

        return paymentTemplateRepository.save(paymentTemplate);
    }

    @Transactional
    public PaymentTemplate updatePaymentTemplate(Long id, PaymentTemplateRequest request) {
        validatePaymentTemplateRequest(request);

        PaymentTemplate paymentTemplate = getPaymentTemplateById(id);

        paymentTemplate.setName(request.name());
        paymentTemplate.setDescription(request.description());
        paymentTemplate.setFromAccountId(request.fromAccountId());
        paymentTemplate.setToAccountId(request.toAccountId());
        paymentTemplate.setAmount(request.amount());
        paymentTemplate.setIsRecurring(request.isRecurring());
        paymentTemplate.setPaymentType(request.paymentType());
        paymentTemplate.setRecurrenceCron(request.recurrenceCron());

        return paymentTemplateRepository.save(paymentTemplate);
    }

    @Transactional
    public void deletePaymentTemplate(Long id) {
        PaymentTemplate paymentTemplate = getPaymentTemplateById(id);
        paymentTemplate.setIsActive(Boolean.FALSE);
        paymentTemplateRepository.save(paymentTemplate);
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

    private void validatePaymentTemplateRequest(PaymentTemplateRequest req) {
        if (req == null) throw new InvalidPaymentRequestException("PaymentTemplate request cannot be null");
        if (req.fromAccountId().equals(req.toAccountId()))
            throw new InvalidPaymentRequestException("Account IDs must not be the same");
    }
}
