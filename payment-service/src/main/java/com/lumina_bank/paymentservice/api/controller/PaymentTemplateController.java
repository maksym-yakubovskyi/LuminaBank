package com.lumina_bank.paymentservice.api.controller;

import com.lumina_bank.common.security.JwtUtils;
import com.lumina_bank.paymentservice.api.request.PaymentTemplateRequest;
import com.lumina_bank.paymentservice.application.mapper.PaymentTemplateMapper;
import com.lumina_bank.paymentservice.domain.model.PaymentTemplate;
import com.lumina_bank.paymentservice.application.service.PaymentTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/payments/payment_templates")
@RequiredArgsConstructor
@Slf4j
public class PaymentTemplateController {

    private final PaymentTemplateService paymentTemplateService;
    private final PaymentTemplateMapper paymentTemplateMapper;

    @PostMapping
    public ResponseEntity<?> createPaymentTemplate (
            @Valid @RequestBody PaymentTemplateRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = JwtUtils.extractUserId(jwt);

        PaymentTemplate paymentTemplate = paymentTemplateService.createPaymentTemplate(request,userId);

        log.info("Payment template created with id={}", paymentTemplate.getId());

        return ResponseEntity.created(URI.create("/payment_templates/"+paymentTemplate.getId()))
                .body(paymentTemplateMapper.toResponse(paymentTemplate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePaymentTemplate (@PathVariable Long id) {

        paymentTemplateService.deletePaymentTemplate(id);

        log.info("Payment template deleted with id={} (soft delete)", id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyPaymentTemplates(@AuthenticationPrincipal Jwt jwt) {
        Long userId = JwtUtils.extractUserId(jwt);

        List<PaymentTemplate> templates = paymentTemplateService.getTemplatesByUserId(userId);

        return ResponseEntity.ok(paymentTemplateMapper.toResponseList(templates));
    }
}