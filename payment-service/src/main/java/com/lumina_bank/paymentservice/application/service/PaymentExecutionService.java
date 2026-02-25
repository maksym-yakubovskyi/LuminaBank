package com.lumina_bank.paymentservice.application.service;

import com.lumina_bank.common.exception.ServiceCallException;
import com.lumina_bank.paymentservice.domain.model.Payment;
import com.lumina_bank.paymentservice.infrastructure.external.account.FeignAccountGateway;
import com.lumina_bank.paymentservice.infrastructure.external.account.dto.AccountResponse;
import com.lumina_bank.paymentservice.infrastructure.external.transaction.FeignTransactionGateway;
import com.lumina_bank.paymentservice.infrastructure.external.transaction.dto.TransactionRequest;
import com.lumina_bank.paymentservice.infrastructure.external.transaction.dto.TransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentExecutionService {
    private final FeignAccountGateway accountGateway;
    private final FeignTransactionGateway transactionGateway;
    private final NbuExchangeRateService rateService;
    private final PaymentTransactionService stateService;

    public void execute(Payment payment) {

        stateService.markProcessing(payment);

        AccountResponse from = accountGateway.getByCard(payment.getFromCardNumber());
        AccountResponse to = accountGateway.getByCard(payment.getToCardNumber());

        BigDecimal rate = rateService.getRate(from.currency(), to.currency());
        BigDecimal converted = payment.getAmount().multiply(rate);

        payment.setToAccountOwnerId(to.userId());
        payment.setFromAccountId(from.id());
        payment.setToAccountId(to.id());
        payment.setFromCurrency(from.currency());
        payment.setToCurrency(to.currency());
        payment.setConvertedAmount(converted);
        payment.setExchangeRate(rate);

        try{
            TransactionResponse response = transactionGateway.execute(
                    TransactionRequest.builder()
                            .userId(payment.getUserId())
                            .toAccountOwnerId(payment.getToAccountOwnerId())
                            .fromCardNumber(payment.getFromCardNumber())
                            .toCardNumber(payment.getToCardNumber())
                            .fromCurrency(from.currency())
                            .toCurrency(to.currency())
                            .amount(payment.getAmount()) // у валюті відправника
                            .convertedAmount(converted) // у валюті отримувача
                            .exchangeRate(rate)
                            .description(payment.getDescription())
                            .category(payment.getCategory())
                            .build()
            );

            payment.setOutTransactionId(response.outcomingTransactionId());
            payment.setInTransactionId(response.incomingTransactionId());

            stateService.markSuccess(payment);
        }catch (ServiceCallException e){
            log.error("Transaction execution failed for payment {}", payment.getId(), e);
            stateService.markFailed(payment);
        }
    }
}
