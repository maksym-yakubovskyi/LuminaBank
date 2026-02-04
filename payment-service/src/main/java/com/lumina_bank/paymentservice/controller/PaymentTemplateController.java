package com.lumina_bank.paymentservice.controller;

import com.lumina_bank.paymentservice.dto.PaymentTemplateRequest;
import com.lumina_bank.paymentservice.dto.PaymentTemplateResponse;
import com.lumina_bank.common.exception.JwtMissingException;
import com.lumina_bank.paymentservice.model.PaymentTemplate;
import com.lumina_bank.paymentservice.service.payment.PaymentTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/payments/payment_templates")
@RequiredArgsConstructor
@Slf4j
public class PaymentTemplateController {

    private final PaymentTemplateService paymentTemplateService;

    @PostMapping
    public ResponseEntity<?> createPaymentTemplate (
            @Valid @RequestBody PaymentTemplateRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        log.info("POST /payments/payment_templates - Create PaymentTemplate");

        if (jwt == null) throw new JwtMissingException("JWT token is required");
        Long userId = Long.valueOf(jwt.getSubject());

        PaymentTemplate paymentTemplate = paymentTemplateService.createPaymentTemplate(request,userId);

        log.info("Payment template created with id={}", paymentTemplate.getId());

        return ResponseEntity.created(URI.create("/payment_templates/"+paymentTemplate.getId()))
                .body(PaymentTemplateResponse.fromEntity(paymentTemplate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePaymentTemplate (@PathVariable Long id) {
        log.info("DELETE /payments/payment_templates - Delete PaymentTemplate with id={}", id);

        paymentTemplateService.deletePaymentTemplate(id);

        log.info("Payment template deleted with id={} (soft delete)", id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyPaymentTemplates(@AuthenticationPrincipal Jwt jwt) {
        log.info("GET /payments/payment_templates/my - Get My PaymentTemplates");

        if (jwt == null) throw new JwtMissingException("JWT token is required");
        Long userId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(paymentTemplateService.getTemplatesByUserId(userId));
    }
}