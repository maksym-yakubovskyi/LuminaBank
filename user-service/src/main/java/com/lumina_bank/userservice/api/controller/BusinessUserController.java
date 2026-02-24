package com.lumina_bank.userservice.api.controller;

import com.lumina_bank.common.security.JwtUtils;
import com.lumina_bank.userservice.api.request.BusinessUserUpdateRequest;
import com.lumina_bank.userservice.application.mapper.BusinessUserMapper;
import com.lumina_bank.userservice.domain.enums.BusinessCategory;
import com.lumina_bank.userservice.domain.model.BusinessUser;
import com.lumina_bank.userservice.application.service.BusinessUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/business-users")
@RequiredArgsConstructor
@Slf4j
public class BusinessUserController {
    private final BusinessUserService businessUserService;
    private final BusinessUserMapper businessUserMapper;

    @GetMapping("/providers")
    public ResponseEntity<?> getProviders(@RequestParam(required = false) BusinessCategory category){
        List<BusinessUser> providers = businessUserService.getProviders(category);

        return ResponseEntity.ok(businessUserMapper.toProviderResponseList(providers));
    }

    @GetMapping("/my")
    public ResponseEntity<?> getBusinessUser(@AuthenticationPrincipal Jwt jwt) {
        Long userId = JwtUtils.extractUserId(jwt);

        BusinessUser bUser = businessUserService.getBusinessUserById(userId);

        log.info("User fetched id={}", bUser.getId());

        return ResponseEntity.ok().body(businessUserMapper.toResponse(bUser));
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateBusinessUser(@AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody BusinessUserUpdateRequest request) {
        Long userId = JwtUtils.extractUserId(jwt);

        BusinessUser bUser = businessUserService.updateBusinessUser(userId, request);

        log.info("User updated id={}", bUser.getId());

        return ResponseEntity.ok().body(businessUserMapper.toResponse(bUser));
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteBusinessUser(@AuthenticationPrincipal Jwt jwt) {
        Long userId = JwtUtils.extractUserId(jwt);

        businessUserService.deleteBusinessUser(userId);

        log.info("User deleted id={} (soft delete)", userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/name/{id}")
    public ResponseEntity<?> getBusinessUserNameById(@PathVariable Long id){
        log.info("GET /businessusers/name/id - Fetching business user with id={}", id);

        return ResponseEntity.ok(businessUserService.getBusinessUserNameById(id));
    }
}
