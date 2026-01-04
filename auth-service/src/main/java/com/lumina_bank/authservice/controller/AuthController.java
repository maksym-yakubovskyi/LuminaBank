package com.lumina_bank.authservice.controller;

import com.lumina_bank.authservice.dto.*;
import com.lumina_bank.authservice.model.User;
import com.lumina_bank.authservice.service.AuthService;
import com.lumina_bank.authservice.service.EmailVerificationService;
import com.lumina_bank.authservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        log.info("POST /auth/login - Login request received for email: {}", req.email());

        TokensResponse tokensResponse = authService.login(req);

        log.info("User successfully authenticated for email: {}", req.email());

        return ResponseEntity.ok(tokensResponse);
    }

    @PostMapping("/register/user")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUserRequest req) {
        log.info("POST /auth/register - Register request received for email: {}", req.email());

        User user = userService.registerUser(req);

        log.info("User successfully registered for email: {}, userId={}", user.getEmail(), user.getId());

        return ResponseEntity.created(URI.create("/auth/register/user")).build();
    }

//    @PostMapping("/register/user/business")
//    public ResponseEntity<?> register(@Valid @RequestBody RegisterBusinessUserRequest req) {
//        log.info("POST /auth/register - Register request received for email: {}", req.email());
//
//        User user = userService.registerUser(req);
//
//        log.info("User successfully registered for email: {}, userId={}", user.getEmail(), user.getId());
//
//        return ResponseEntity.created(URI.create("/auth/register")).build();
//    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());
        String sid = jwt.getClaim("sid");

        log.info("POST /auth/logout - Logout request received for userId={}, sessionId={}", userId, sid);

        authService.logout(userId, sid);

        log.info("User successfully logged out for userId={}, sessionId={}", userId, sid);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout/all")
    public ResponseEntity<?> logoutAll(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());

        log.info("POST /auth/logout/all - Logout all sessions request received for userId={}", userId);

        authService.logoutAll(userId);

        log.info("User successfully logged out all sessions for userId={}", userId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verificationCode")
    public ResponseEntity<?> sendCode(@Valid @RequestBody EmailRequest req) {
        log.info("POST /auth/sendVerificationCode - Request received for email={}", req.email());

        emailVerificationService.sendVerificationCode(req.email());

        log.info("Verification code sent for email={}", req.email());
        return ResponseEntity.ok().build();
    }
}