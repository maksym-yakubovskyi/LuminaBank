package com.lumina_bank.aiassistantservice.service.client.payment;

import com.lumina_bank.aiassistantservice.domain.dto.client.payment.*;
import com.lumina_bank.aiassistantservice.domain.exception.ExternalServiceException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeignPaymentGateway {
    private final PaymentClientService client;

    public List<TransactionHistoryItemDto> getHistory(
            Integer limit,
            Long accountId,
            boolean all
    ) {
        try {
            ResponseEntity<List<TransactionHistoryItemDto>> response;

            if (all) {
                response = client.getAllHistory(accountId);
            } else {
                response = client.getHistoryLimit(limit, accountId);
            }

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ExternalServiceException("Payment service returned " + response.getStatusCode());
            }

            return Optional.ofNullable(response.getBody()).orElse(List.of());

        } catch (FeignException e) {
            throw new ExternalServiceException(
                    "Сервіс оплат тимчасово недоступний"
            );
        }
    }

    public PaymentResponse makePayment(PaymentRequest request){
        try{
            var response = client.makePayment(request);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ExternalServiceException("Payment service returned " + response.getStatusCode());
            }
            return response.getBody();
        } catch (FeignException e) {
            throw new ExternalServiceException(
                    "Сервіс оплат тимчасово недоступний"
            );
        }
    }

    public PaymentResponse makePaymentService(ServicePaymentRequest request){
        try{
            var response = client.makePaymentService(request);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ExternalServiceException("Payment service returned " + response.getStatusCode());
            }

            return response.getBody();
        } catch (FeignException e) {
            throw new ExternalServiceException(
                    "Сервіс оплат тимчасово недоступний"
            );
        }
    }

    public void makePaymentTemplate(Long templateId){
        try{
            var response = client.makePaymentTemplate(templateId);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ExternalServiceException("Payment service returned " + response.getStatusCode());
            }
        } catch (FeignException e) {
            throw new ExternalServiceException(
                    "Сервіс оплат тимчасово недоступний"
            );
        }
    }

    public List<PaymentTemplateResponse> getPaymentTemplates(){
        try{
            var response = client.getMyPaymentTemplates();

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ExternalServiceException("Payment service returned " + response.getStatusCode());
            }

            return Optional.ofNullable(response.getBody()).orElse(List.of());
        }catch (FeignException e) {
            throw new ExternalServiceException(
                    "Сервіс оплат тимчасово недоступний"
            );
        }
    }
}
