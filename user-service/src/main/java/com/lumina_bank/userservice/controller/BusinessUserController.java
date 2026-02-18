package com.lumina_bank.userservice.controller;

import com.lumina_bank.common.exception.JwtMissingException;
import com.lumina_bank.userservice.dto.BusinessUserResponse;
import com.lumina_bank.userservice.dto.BusinessUserUpdateDto;
import com.lumina_bank.userservice.enums.BusinessCategory;
import com.lumina_bank.userservice.model.BusinessUser;
import com.lumina_bank.userservice.service.BusinessUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/my")
    public ResponseEntity<?> getBusinessUser(@AuthenticationPrincipal Jwt jwt) {
        log.info("GET /users/business-users/my - Fetching business user");

        if (jwt == null) throw new JwtMissingException("JWT token is required");
        Long userId = Long.valueOf(jwt.getSubject());

        BusinessUser bUser = businessUserService.getBusinessUserById(userId);

        log.info("User fetched id={}", bUser.getId());

        return ResponseEntity.ok().body(BusinessUserResponse.fromEntity(bUser));
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateBusinessUser(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody BusinessUserUpdateDto bUserDto) {
        log.info("PUT /users/business-users/me - Received request to update business user");

        if (jwt == null) throw new JwtMissingException("JWT token is required");
        Long userId = Long.valueOf(jwt.getSubject());

        BusinessUser bUser = businessUserService.updateBusinessUser(userId, bUserDto);

        log.info("User updated id={}", bUser.getId());

        return ResponseEntity.ok().body(BusinessUserResponse.fromEntity(bUser));
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteBusinessUser(@AuthenticationPrincipal Jwt jwt) {
        log.info("DELETE /users/business-users/me - Deleting user");

        if (jwt == null) throw new JwtMissingException("JWT token is required");
        Long userId = Long.valueOf(jwt.getSubject());

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
