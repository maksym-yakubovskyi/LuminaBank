package com.lumina_bank.paymentservice.application.service;

import com.lumina_bank.paymentservice.api.request.PaymentRequest;
import com.lumina_bank.paymentservice.api.request.ServicePaymentRequest;
import com.lumina_bank.paymentservice.api.response.TransactionHistoryItemResponse;
import com.lumina_bank.paymentservice.application.mapper.TransactionHistoryMapper;
import com.lumina_bank.paymentservice.domain.exception.*;
import com.lumina_bank.paymentservice.domain.util.PaymentDescriptionBuilder;
import com.lumina_bank.paymentservice.infrastructure.external.account.FeignAccountGateway;
import com.lumina_bank.paymentservice.domain.enums.PaymentStatus;
import com.lumina_bank.paymentservice.domain.model.Payment;
import com.lumina_bank.paymentservice.domain.model.PaymentTemplate;
import com.lumina_bank.paymentservice.domain.repository.PaymentRepository;
import com.lumina_bank.paymentservice.domain.repository.PaymentTemplateRepository;
import com.lumina_bank.paymentservice.application.mapper.PaymentCreatedEventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentExecutionService executionService;
    private final PaymentTransactionService stateService;
    private final PaymentTemplateRepository templateRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PaymentCreatedEventMapper eventMapper;
    private final FeignAccountGateway accountGateway;

    private final TransactionHistoryMapper  transactionHistoryMapper;
    private final PaymentDescriptionBuilder descriptionBuilder;

    private static final Duration CANCEL_WINDOW = Duration.ofSeconds(30);// час за який можна скасувати оплату

    @Transactional
    public Payment createTransfer(PaymentRequest request,Long userId) {
        validateCards(request.fromCardNumber(), request.toCardNumber());

        // Створення Платежу зі статусом PENDING
        Payment payment = Payment.builder()
                .userId(userId)
                .fromCardNumber(request.fromCardNumber())
                .toCardNumber(request.toCardNumber())
                .amount(request.amount())
                .description(request.description())
                .paymentStatus(PaymentStatus.PENDING)
                .category("TRANSFER")
                .build();

        payment = paymentRepository.save(payment);

        log.debug("Payment saved with id={} and status={}", payment.getId(), payment.getPaymentStatus());

        eventPublisher.publishEvent(eventMapper.to(payment));

        return payment;
    }

    @Transactional
    public Payment createServicePayment(ServicePaymentRequest request, Long userId) {
        String providerCard = accountGateway.getMerchantCard(request.providerId());

        validateCards(request.fromCardNumber(), providerCard);

        String finalDescription = descriptionBuilder.buildServiceDescription(
                request.description(),
                request.payerReference()
        );

        Payment payment = Payment.builder()
                .userId(userId)
                .fromCardNumber(request.fromCardNumber())
                .toCardNumber(providerCard)
                .amount(request.amount())
                .description(finalDescription)
                .paymentStatus(PaymentStatus.PENDING)
                .category(request.category())
                .build();

        payment = paymentRepository.save(payment);

        log.debug("Service payment saved with id={} status={}", payment.getId(), payment.getPaymentStatus());

        eventPublisher.publishEvent(eventMapper.to(payment));
        return payment;
    }

    @Transactional
    public void executeTemplate(Long templateId,Long userId) {
        PaymentTemplate template = templateRepository
                .findByIdAndIsActiveTrue(templateId)
                .orElseThrow(() -> new PaymentTemplateNotFoundException("Template not found"));

        if(!template.getUserId().equals(userId)){
            throw new PaymentTemplateAccessDeniedException("Incorrect user id");
        }

        Payment payment = stateService.createPendingPayment(template);

        executionService.execute(payment);
    }

    @Transactional
    public Payment cancelPayment(Long paymentId) {
        Payment payment = getPayment(paymentId);

        if (payment.getTemplate() != null && payment.getTemplate().getIsRecurring()) {
            throw new PaymentCancellationException("Recurring payments cannot be canceled");
        }

        if (payment.getPaymentStatus() != PaymentStatus.PENDING)
            throw new PaymentStateConflictException("Payment cannot be canceled");

        if (Duration.between(payment.getCreatedAt(), LocalDateTime.now()).compareTo(CANCEL_WINDOW) > 0) {
            throw new PaymentCancellationException("Cancellation window expired");
        }

        payment.setPaymentStatus(PaymentStatus.CANCELLED);
        return paymentRepository.save(payment);
    }

    @Transactional(readOnly = true)
    public Payment getPayment(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + paymentId));
    }

    @Transactional(readOnly = true)
    public List<TransactionHistoryItemResponse> getUserHistory(Long userId, Long accountId, int limit) {
        Pageable pageable = PageRequest.of(
                0,
                limit,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Payment> page =
                paymentRepository.findUserHistory(userId, accountId, pageable);

        return page.getContent().stream()
                .map(p -> transactionHistoryMapper.toHistoryItem(p, userId,accountId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TransactionHistoryItemResponse> getUserHistory(Long userId, Long accountId) {
        return paymentRepository.findUserHistory(userId, accountId)
                .stream()
                .map(p -> transactionHistoryMapper.toHistoryItem(p, userId,accountId))
                .toList();
    }

    // PRIVATE HELPERS
    private void validateCards(String fromCardNumber, String toCardNumber) {
        if (fromCardNumber.equals(toCardNumber))
            throw new InvalidPaymentRequestException("Card numbers must not be the same");
    }
}