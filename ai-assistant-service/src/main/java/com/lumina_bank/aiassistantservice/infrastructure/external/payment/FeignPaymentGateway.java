package com.lumina_bank.aiassistantservice.infrastructure.external.payment;

import com.lumina_bank.aiassistantservice.infrastructure.external.payment.dto.*;
import com.lumina_bank.aiassistantservice.infrastructure.external.FeignExceptionMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeignPaymentGateway {
    private final PaymentClientService client;
    private final FeignExceptionMapper mapper;

    public List<TransactionHistoryItemResponse> getHistory(
            Integer limit,
            Long accountId,
            boolean all
    ) {
        try {
            if (all) {
                return Optional.ofNullable(client.getAllHistory(accountId))
                        .orElse(List.of());
            } else {
                return Optional.ofNullable(client.getHistoryLimit(limit, accountId))
                        .orElse(List.of());
            }
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }

    public PaymentResponse makePayment(PaymentRequest request){
        try{
            return client.makePayment(request);
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }

    public PaymentResponse makePaymentService(ServicePaymentRequest request){
        try{
            return client.makePaymentService(request);
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }

    public void makePaymentTemplate(Long templateId){
        try{
            client.makePaymentTemplate(templateId);
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }

    public List<PaymentTemplateResponse> getPaymentTemplates(){
        try{
            return Optional.ofNullable(client.getMyPaymentTemplates()).orElse(List.of());
        }catch (FeignException e) {
            throw mapper.map(e);
        }
    }
}
