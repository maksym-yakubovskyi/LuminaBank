package com.lumina_bank.userservice.controller;

import com.lumina_bank.userservice.dto.UserResponse;
import com.lumina_bank.userservice.dto.UserUpdateDto;
import com.lumina_bank.userservice.model.User;
import com.lumina_bank.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

//    @PostMapping
//    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateDto userDto) {
//        log.info("POST /users - Received request to create user : {}", userDto.email());
//
//        User user = userService.createUser(userDto);
//
//        log.info("User created id={}", user.getId());
//
//        return ResponseEntity.created(URI.create("/users/" + user.getId()))
//                .body(UserResponse.fromEntity(user));
//    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        log.info("GET /users/{id} - Fetching user with id={}", id);

        User user = userService.getUserById(id);

        log.info("User fetched id={}", user.getId());

        return ResponseEntity.ok().body(UserResponse.fromEntity(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDto userDto) {
        log.info("PUT /users/{id} - Received request to update user with id={}", id);

        User user = userService.updateUser(id, userDto);

        log.info("User updated id={}", user.getId());

        return ResponseEntity.ok().body(UserResponse.fromEntity(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        log.info("DELETE /users/{id} - Deleting user with id={}", id);

        userService.deleteUser(id);

        log.info("User deleted id={} (soft delete)", id);
        return ResponseEntity.noContent().build();
    }
}
