package com.lumina_bank.aiassistantservice.infrastructure.external.payment;

import com.lumina_bank.aiassistantservice.infrastructure.external.payment.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "payment-service", path = "/payments",configuration = FeignClientProperties.FeignClientConfiguration.class)
public interface PaymentClientService {

    @GetMapping("/history/limit")
    List<TransactionHistoryItemResponse> getHistoryLimit(@RequestParam(required = false) Integer limit, @RequestParam(required = false) Long accountId);

    @GetMapping("/history/all")
    List<TransactionHistoryItemResponse> getAllHistory(@RequestParam(required = false) Long accountId);

    @PostMapping
    PaymentResponse makePayment(@RequestBody PaymentRequest request);

    @PostMapping("/service")
    PaymentResponse makePaymentService(@RequestBody ServicePaymentRequest request);

    @PostMapping("/template/{templateId}")
    void makePaymentTemplate (@PathVariable Long templateId);

    @GetMapping("/payment_templates/my")
    List<PaymentTemplateResponse> getMyPaymentTemplates();
}
