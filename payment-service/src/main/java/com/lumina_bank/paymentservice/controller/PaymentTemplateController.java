package com.lumina_bank.paymentservice.controller;

import com.lumina_bank.paymentservice.dto.PaymentTemplateRequest;
import com.lumina_bank.paymentservice.dto.PaymentTemplateResponse;
import com.lumina_bank.paymentservice.model.PaymentTemplate;
import com.lumina_bank.paymentservice.service.payment.PaymentTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/payment_templates")
@RequiredArgsConstructor
@Slf4j
public class PaymentTemplateController {

    private final PaymentTemplateService paymentTemplateService;

    @PostMapping
    public ResponseEntity<?> createPaymentTemplate (@Valid @RequestBody PaymentTemplateRequest request) {
        log.info("POST /payment_templates - Create PaymentTemplate");

        PaymentTemplate paymentTemplate = paymentTemplateService.createPaymentTemplate(request);

        log.info("Payment template created with id={}", paymentTemplate.getId());

        return ResponseEntity.created(URI.create("/payment_templates/"+paymentTemplate.getId()))
                .body(PaymentTemplateResponse.fromEntity(paymentTemplate));
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updatePaymentTemplate (
            @PathVariable Long id,
            @Valid @RequestBody PaymentTemplateRequest request) {
        log.info("PUT /payment_templates - Update PaymentTemplate");

        PaymentTemplate template = paymentTemplateService.updatePaymentTemplate(id, request);

        log.info("Payment template updated with id={}", template.getId());

        return ResponseEntity.ok(PaymentTemplateResponse.fromEntity(template));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePaymentTemplate (@PathVariable Long id) {
        log.info("DELETE /payment_templates - Delete PaymentTemplate with id={}", id);

        paymentTemplateService.deletePaymentTemplate(id);

        log.info("Payment template deleted with id={} (soft delete)", id);

        return ResponseEntity.noContent().build();
    }

}
