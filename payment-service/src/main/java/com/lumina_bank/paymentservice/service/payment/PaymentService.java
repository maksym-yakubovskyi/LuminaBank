package com.lumina_bank.paymentservice.service.payment;

import com.lumina_bank.common.exception.BusinessException;
import com.lumina_bank.paymentservice.dto.PaymentRequest;
import com.lumina_bank.paymentservice.dto.ServicePaymentRequest;
import com.lumina_bank.paymentservice.dto.TransactionHistoryItemDto;
import com.lumina_bank.paymentservice.dto.client.AccountResponse;
import com.lumina_bank.paymentservice.dto.client.TransactionRequest;
import com.lumina_bank.paymentservice.enums.PaymentStatus;
import com.lumina_bank.paymentservice.exception.*;
import com.lumina_bank.paymentservice.model.Payment;
import com.lumina_bank.paymentservice.model.PaymentTemplate;
import com.lumina_bank.paymentservice.repository.PaymentRepository;
import com.lumina_bank.paymentservice.repository.PaymentTemplateRepository;
import com.lumina_bank.paymentservice.service.client.AccountClientService;
import com.lumina_bank.paymentservice.service.client.TransactionClientService;
import com.lumina_bank.paymentservice.service.rate.NbuExchangeRateService;
import com.lumina_bank.paymentservice.service.util.PaymentCreatedEventFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TransactionClientService transactionClientService;
    private final AccountClientService accountClientService;
    private final PaymentTemplateRepository paymentTemplateRepository;
    private final PaymentTransactionService paymentTransactionService;
    private final NbuExchangeRateService nbuExchangeRateService;
    private final ApplicationEventPublisher eventPublisher;
    private final PaymentCreatedEventFactory paymentCreatedEventFactory;

    private static final Duration CANCEL_WINDOW = Duration.ofSeconds(30);// час за який можна скасувати оплату

    @Transactional(readOnly = true)
    public List<TransactionHistoryItemDto> getUserHistory(Long accountId,int count){
        Pageable pageable = PageRequest.of(
                0,
                count,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<Payment> page = paymentRepository.findByFromAccountIdOrToAccountId(accountId,accountId, pageable);

        return page.getContent().stream()
                .map(p -> TransactionHistoryItemDto.toHistoryItem(p, accountId))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TransactionHistoryItemDto> getUserHistory(Long accountId){
        return paymentRepository.findByFromAccountIdOrToAccountId(accountId,accountId).stream()
                .map(p -> TransactionHistoryItemDto.toHistoryItem(p,accountId))
                .toList();
    }

    @Transactional(readOnly = true)
    public Payment getPayment(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + paymentId));
    }


    public void makeTemplatePayment(Long templateId) {
        PaymentTemplate template = paymentTemplateRepository.findByIdAndIsActiveTrue(templateId)
                .orElseThrow(() -> new PaymentTemplateNotFoundException("Payment template with id " + templateId + " not found"));

        makePayment(template);
    }

    @Transactional
    public Payment makePayment(PaymentRequest paymentRequest,Long userId) {
        if (paymentRequest.fromCardNumber().equals(paymentRequest.toCardNumber()))
            throw new InvalidPaymentRequestException("Card numbers must not be the same");

        PaymentTemplate template = null;
        if (paymentRequest.templateId() != null) {
            template = paymentTemplateRepository
                    .findByIdAndIsActiveTrue(paymentRequest.templateId())
                    .orElse(null);
        }

        // Створення Платежу зі статусом PENDING
        Payment payment = Payment.builder()
                .userId(userId)
                .fromCardNumber(paymentRequest.fromCardNumber())
                .toCardNumber(paymentRequest.toCardNumber())
                .amount(paymentRequest.amount())
                .description(paymentRequest.description())
                .paymentStatus(PaymentStatus.RISK_PENDING)
                .template(template)
                .build();

        payment = paymentRepository.save(payment);

        log.debug("Payment saved with id={} and status={}", payment.getId(), payment.getPaymentStatus());

        eventPublisher.publishEvent(paymentCreatedEventFactory.from(payment));
        return payment;
    }

    public void makePayment(PaymentTemplate paymentTemplate) {
        Payment payment = paymentTransactionService.createPendingPayment(paymentTemplate);
        executePayment(payment);
    }

    @Transactional
    public Payment makeServicePayment(ServicePaymentRequest request, Long userId) {
        String providerCardNumber = getProviderCardNumber(request.providerId());

        if (request.fromCardNumber().equals(providerCardNumber)) {
            throw new InvalidPaymentRequestException("Card numbers must not be the same");
        }

        String finalDescription = buildServiceDescription(
                request.description(),
                request.payerReference()
        );

        Payment payment = Payment.builder()
                .userId(userId)
                .fromCardNumber(request.fromCardNumber())
                .toCardNumber(providerCardNumber)
                .amount(request.amount())
                .description(finalDescription)
                .paymentStatus(PaymentStatus.RISK_PENDING)
                .build();

        payment = paymentRepository.save(payment);

        log.debug("Service payment saved with id={} status={}", payment.getId(), payment.getPaymentStatus());

        eventPublisher.publishEvent(paymentCreatedEventFactory.from(payment));
        return payment;
    }

    @Transactional
    public Payment cancelPayment(Long paymentId) {
        Payment payment = getPayment(paymentId);

        if (payment.getTemplate() != null && payment.getTemplate().getIsRecurring()) {
            throw new PaymentCancellationException("Recurring payments cannot be canceled");
        }

        if (!payment.getPaymentStatus().equals(PaymentStatus.PENDING)) {
            throw new PaymentStateConflictException("Only PENDING payments can be canceled");
        }

        if (Duration.between(payment.getCreatedAt(), LocalDateTime.now()).compareTo(CANCEL_WINDOW) > 0) {
            throw new PaymentCancellationException("Cancellation window expired");
        }

        payment.setPaymentStatus(PaymentStatus.CANCELLED);
        return paymentRepository.save(payment);
    }

    @Scheduled(fixedDelay = 5000)
    public void executePendingPayments() {
        List<Payment> payments = paymentRepository
                .findAllByPaymentStatusAndCreatedAtBefore(PaymentStatus.PENDING, LocalDateTime.now().minus(CANCEL_WINDOW));

        log.debug("Found {} pending payments to process", payments.size());

        log.debug("Method executePendingPayments is called at : {}", LocalDateTime.now());

        for (Payment payment : payments) {
            try {
                executePayment(payment);
            } catch (BusinessException e) {
                log.warn("Business exception occurred during processing of payment with id={}: {}", payment.getId(), e.getMessage());
            } catch (Exception e) {
                log.warn("Unexpected error occurred during processing of payment with id={}: {}", payment.getId(), e.getMessage());
            }
        }
    }

    public void executePayment(Payment payment) {
        paymentTransactionService.markProcessing(payment);

        AccountResponse from = getAccount(payment.getFromCardNumber());
        AccountResponse to = getAccount(payment.getToCardNumber());

        if (from == null || to == null) {
            log.warn("Failed to fetch account currency: from={}, to={}", from, to);

            paymentTransactionService.updatePaymentStatus(payment, PaymentStatus.FAILED);

            throw new ExternalServiceException("Unable to fetch account currency");
        }

        BigDecimal rate = nbuExchangeRateService.getRate(from.currency(), to.currency());
        BigDecimal convertedAmount = payment.getAmount().multiply(rate);

        payment.setFromAccountId(from.id());
        payment.setToAccountId(to.id());
        payment.setFromCurrency(from.currency());
        payment.setToCurrency(to.currency());
        payment.setConvertedAmount(convertedAmount);
        payment.setExchangeRate(rate);

        try {
            var response = transactionClientService.makeTransaction(
                    TransactionRequest.builder()
                            .fromCardNumber(payment.getFromCardNumber())
                            .fromCurrency(from.currency())
                            .toCurrency(to.currency())
                            .toCardNumber(payment.getToCardNumber())
                            .amount(payment.getAmount()) // у валюті відправника
                            .convertedAmount(convertedAmount) // у валюті отримувача
                            .exchangeRate(rate)
                            .description(payment.getDescription())
                            .build()
            );

            if (response == null || response.getBody() == null) {
                log.warn("Null response from transaction service for payment id={}", payment.getId());
                throw new ExternalServiceException("Null response from transaction service");
            }

            if (response.getStatusCode().is2xxSuccessful()) {
                payment.setTransactionId(response.getBody().id());
                paymentTransactionService.updatePaymentStatus(payment, PaymentStatus.SUCCESS);
            } else {
                paymentTransactionService.updatePaymentStatus(payment, PaymentStatus.FAILED);

                log.warn("Payment id={} failed, status={} body={}",
                        payment.getId(), response.getStatusCode(), response.getBody());

                throw new ExternalServiceException("Transaction service returned non-2xx: "
                        + response.getStatusCode() + ", and: " + response.getBody());
            }
        } catch (BusinessException e) {
            log.warn("Business exception during payment id={}: {}", payment.getId(), e.getMessage());

            paymentTransactionService.updatePaymentStatus(payment, PaymentStatus.FAILED);
            throw e;
        } catch (Exception e) {
            log.warn("Error executing payment id={}: {}", payment.getId(), e.getMessage());

            paymentTransactionService.updatePaymentStatus(payment, PaymentStatus.FAILED);
            throw new ExternalServiceException("Payment execution failed", e);
        }
    }

    private AccountResponse getAccount(String cardNumber) {
        try {
            var response = accountClientService.getAccountByCardNumber(cardNumber);

            if (response == null) {
                log.warn("Null response from account service ");
                return null;
            }

            if (response.getStatusCode().is2xxSuccessful()) {
                AccountResponse body = response.getBody();
                if (body != null && body.currency() != null) {
                    return body;
                } else {
                    log.warn("Empty or invalid account response body");
                    return null;
                }
            } else {
                log.warn("Account service returned non-2xx  status={}, body={}",
                        response.getStatusCode(), response.getBody());
                return null;
            }
        } catch (Exception e) {
            log.warn("Failed to fetch account currency : {}", e.getMessage(), e);
            return null;
        }
    }

    public String getProviderCardNumber(Long providerId) {
        try {
            var response = accountClientService.getMerchantCardNumber(providerId);

            if (response == null || !response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new ExternalServiceException("Cannot fetch provider merchant card number");
            }

            String cardNumber = response.getBody().cardNumber();

            if (cardNumber == null || cardNumber.isBlank()) {
                throw new ExternalServiceException("Provider merchant card number is empty");
            }

            return cardNumber;
        } catch (Exception e) {
            log.warn("Failed to fetch account currency : {}", e.getMessage(), e);
            throw new ExternalServiceException("Failed to get account currency", e);
        }
    }

    public String buildServiceDescription(String description, String payerReference) {
        String base = (description == null || description.isBlank())
                ? "Service payment"
                : description;

        return base + " | Ref: " + payerReference;
    }
}