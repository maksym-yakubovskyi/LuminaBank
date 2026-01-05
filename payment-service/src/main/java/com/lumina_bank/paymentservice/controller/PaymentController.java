package com.lumina_bank.paymentservice.controller;

import com.lumina_bank.paymentservice.dto.PaymentRequest;
import com.lumina_bank.paymentservice.dto.PaymentResponse;
import com.lumina_bank.paymentservice.exception.JwtMissingException;
import com.lumina_bank.paymentservice.model.Payment;
import com.lumina_bank.paymentservice.service.payment.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<?> makePayment(
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal Jwt jwt){
        log.info("POST /payments - Making Payment");

        if (jwt == null) throw new JwtMissingException("JWT token is required");
        Long userId = Long.valueOf(jwt.getSubject());

        Payment  payment = paymentService.makePayment(request,userId);

        log.info("Payment created with id={}", payment.getId());

        return ResponseEntity.ok(PaymentResponse.fromEntity(payment));
    }

    @DeleteMapping("/{paymentId}/cancel")
    public ResponseEntity<?> cancelPayment(@PathVariable Long paymentId){
        log.info("DELETE /payments/{paymentId} - Cancel Payment");

        return ResponseEntity.ok(PaymentResponse.fromEntity(paymentService.cancelPayment(paymentId)));
    }

    @GetMapping("/history/{accountId}/limit")
    public ResponseEntity<?> getHistoryLimit(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "5") int limit){
        log.info("GET /payments/history/{accountId}/limit/{limit} - Get history limit={}",limit);

        return ResponseEntity.ok(paymentService.getUserHistory(accountId,limit));
    }
}
