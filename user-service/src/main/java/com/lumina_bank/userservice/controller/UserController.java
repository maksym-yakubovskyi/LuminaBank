package com.lumina_bank.userservice.controller;

import com.lumina_bank.userservice.dto.UserResponse;
import com.lumina_bank.userservice.dto.UserUpdateDto;
import com.lumina_bank.userservice.model.User;
import com.lumina_bank.userservice.service.UserService;
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

    @GetMapping("/me")
    public ResponseEntity<?> getUser(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());

        log.info("GET /users/{id} - Fetching user with id={}", userId);

        User user = userService.getUserById(userId);

        log.info("User fetched id={}", user.getId());

        return ResponseEntity.ok().body(UserResponse.fromEntity(user));
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateUser(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UserUpdateDto userDto) {
        Long userId = Long.valueOf(jwt.getSubject());

        log.info("PUT /users/me - Received request to update user with id={}", userId);

        User user = userService.updateUser(userId, userDto);

        log.info("User updated id={}", user.getId());

        return ResponseEntity.ok().body(UserResponse.fromEntity(user));
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());

        log.info("DELETE /users/me - Deleting user with id={}", userId);

        userService.deleteUser(userId);

        log.info("User deleted id={} (soft delete)", userId);
        return ResponseEntity.noContent().build();
    }
}
