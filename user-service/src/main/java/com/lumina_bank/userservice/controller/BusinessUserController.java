package com.lumina_bank.userservice.controller;

import com.lumina_bank.common.enums.user.BusinessCategory;
import com.lumina_bank.userservice.service.BusinessUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/business-users")
@RequiredArgsConstructor
@Slf4j
public class BusinessUserController {
    private final BusinessUserService businessUserService;

    @GetMapping("/providers")
    public ResponseEntity<?> getProviders(
            @RequestParam(required = false)BusinessCategory category
            ){
        log.info("GET /businessusers/providers - Fetching providers with category={}", category);

        return ResponseEntity.ok(businessUserService.getProviders(category));
    }
}
