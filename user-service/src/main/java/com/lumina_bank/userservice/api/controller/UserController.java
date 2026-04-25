package com.lumina_bank.userservice.api.controller;

import com.lumina_bank.common.security.JwtUtils;
import com.lumina_bank.userservice.api.request.UserUpdateRequest;
import com.lumina_bank.userservice.application.mapper.UserMapper;
import com.lumina_bank.userservice.domain.model.User;
import com.lumina_bank.userservice.application.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/my")
    public ResponseEntity<?> getUser(@AuthenticationPrincipal Jwt jwt) {
        Long userId = JwtUtils.extractUserId(jwt);

        User user = userService.getUserById(userId);

        log.info("User fetched id={}", user.getId());

        return ResponseEntity.ok().body(userMapper.toResponse(user));
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateUser(@AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UserUpdateRequest request) {
        Long userId = JwtUtils.extractUserId(jwt);

        User user = userService.updateUser(userId, request);

        log.info("User updated id={}", user.getId());

        return ResponseEntity.ok().body(userMapper.toResponse(user));
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal Jwt jwt) {
        Long userId = JwtUtils.extractUserId(jwt);

        userService.deleteUser(userId);

        log.info("User deleted id={} (soft delete)", userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok().body(userMapper.toResponse(user));
    }
}