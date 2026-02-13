package com.lumina_bank.aiassistantservice.service.client.payment;

import com.lumina_bank.aiassistantservice.domain.dto.client.payment.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "payment-service", path = "/payments",configuration = FeignClientProperties.FeignClientConfiguration.class)
public interface PaymentClientService {

    @GetMapping("/history/limit")
    ResponseEntity<List<TransactionHistoryItemDto>> getHistoryLimit(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Long accountId
    );

    @GetMapping("/history/all")
    ResponseEntity<List<TransactionHistoryItemDto>> getAllHistory(
            @RequestParam(required = false) Long accountId
    );

    @PostMapping
    ResponseEntity<PaymentResponse> makePayment(@RequestBody PaymentRequest request);

    @PostMapping("/service")
    ResponseEntity<PaymentResponse> makePaymentService(@RequestBody ServicePaymentRequest request);

    @PostMapping("/template/{templateId}")
    ResponseEntity<?> makePaymentTemplate (@PathVariable Long templateId);

    @GetMapping("/payment_templates/my")
    ResponseEntity<List<PaymentTemplateResponse>> getMyPaymentTemplates();
}
