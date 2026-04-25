package com.lumina_bank.paymentservice.api.controller;

import com.lumina_bank.common.security.JwtUtils;
import com.lumina_bank.paymentservice.api.request.PaymentRequest;
import com.lumina_bank.paymentservice.api.request.ServicePaymentRequest;
import com.lumina_bank.paymentservice.application.mapper.PaymentMapper;
import com.lumina_bank.paymentservice.domain.enums.PaymentStatus;
import com.lumina_bank.paymentservice.domain.model.Payment;
import com.lumina_bank.paymentservice.application.service.PaymentService;
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
    private final PaymentMapper paymentMapper;

    @PostMapping
    public ResponseEntity<?> makePayment(
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal Jwt jwt){
        Long userId = JwtUtils.extractUserId(jwt);

        Payment  payment = paymentService.createTransfer(request,userId);

        log.info("Payment created, id={}, userId={}", payment.getId(), userId);

        return ResponseEntity.ok(paymentMapper.toResponse(payment));
    }

    @PostMapping("/service")
    public ResponseEntity<?> makePaymentService(
            @Valid @RequestBody ServicePaymentRequest request,
            @AuthenticationPrincipal Jwt jwt){
        Long userId = JwtUtils.extractUserId(jwt);

        Payment payment = paymentService.createServicePayment(request, userId);

        log.info("Service payment created, id={}, userId={}", payment.getId(), userId);

        return ResponseEntity.ok(paymentMapper.toResponse(payment));
    }

    @PostMapping("/template/{templateId}")
    public ResponseEntity<?> makePaymentTemplate(@PathVariable Long templateId,@AuthenticationPrincipal Jwt jwt){
        Long userId = JwtUtils.extractUserId(jwt);

        paymentService.executeTemplate(templateId,userId);

        log.info("Payment executed by templateId={}", templateId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{paymentId}/cancel")
    public ResponseEntity<?> cancelPayment(@PathVariable Long paymentId){

        Payment payment = paymentService.cancelPayment(paymentId);

        log.info("Payment cancelled, id={}", paymentId);

        return ResponseEntity.ok(paymentMapper.toResponse(payment));
    }

    @GetMapping("/history/limit")
    public ResponseEntity<?> getHistoryLimit(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(required = false) Long accountId){
        Long userId = JwtUtils.extractUserId(jwt);

        return ResponseEntity.ok(paymentService.getUserHistory(userId, accountId, limit));
    }

    @GetMapping("/history/all")
    public ResponseEntity<?> getAllHistory(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) Long accountId){
        Long userId = JwtUtils.extractUserId(jwt);

        return ResponseEntity.ok(paymentService.getUserHistory(userId, accountId));
    }

    @GetMapping("/admin/user/history")
    public ResponseEntity<?> getUserHistory(
            @RequestParam Long userId,
            @RequestParam Long accountId,
            @RequestParam(required = false) PaymentStatus status){
        return ResponseEntity.ok(paymentService.getUserHistory(userId, accountId, status));
    }

    @PostMapping("/admin/approve/{paymentId}")
    public ResponseEntity<?> approvePayment(@PathVariable Long paymentId) {

        paymentService.approvePayment(paymentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/admin/reject/{paymentId}")
    public ResponseEntity<?> rejectPayment(@PathVariable Long paymentId) {

        paymentService.rejectPayment(paymentId);
        return ResponseEntity.ok().build();
    }

}