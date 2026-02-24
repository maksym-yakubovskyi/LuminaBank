package com.lumina_bank.userservice.api.controller;

import com.lumina_bank.userservice.api.response.UserCheckResponse;
import com.lumina_bank.userservice.application.service.UserCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserCheckController {
    private final UserCheckService userCheckService;

    @GetMapping("/{id}/check")
    public ResponseEntity<UserCheckResponse> check(@PathVariable Long id){
        log.info("GET /users/{id}/check - Fetching user with id={}", id);

        return ResponseEntity.ok(userCheckService.checkUser(id));
    }
}