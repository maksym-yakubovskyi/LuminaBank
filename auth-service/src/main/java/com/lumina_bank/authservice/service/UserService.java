package com.lumina_bank.authservice.service;

import com.lumina_bank.authservice.dto.RegisterRequest;
import com.lumina_bank.authservice.exception.*;
import com.lumina_bank.authservice.model.User;
import com.lumina_bank.authservice.repository.UserRepository;
import com.lumina_bank.authservice.security.util.UserRegisteredEventFactory;
import com.lumina_bank.common.enums.user.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final UserRegisteredEventFactory userRegisteredEventFactory;
    private final EmailVerificationService emailVerificationService;

    public User getUserById(Long id) {
        log.debug("Fetching user with id {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id={}", id);
                    return new UserNotFoundException("User not found with id=" + id);
                });

        if (!user.getEnabled()) {
            log.warn("User is disable with id={}", id);
            throw new UserDisabledException("User is disable with id=" + id);
        }

        if (user.getLocked()) {
            log.warn("User is locked with id={}", id);
            throw new UserLockedException("User is locked with id=" + id);
        }

        return user;
    }

    @Transactional
    public User registerUser(RegisterRequest req) {
        log.debug("Attempt to register new user with email={}", req.email());

        emailVerificationService.validateVerificationCode(req.email(), req.verificationCode());

        User userSaved = userRepository.save(
                User.builder()
                        .email(req.email())
                        .passwordHash(passwordEncoder.encode(req.password()))
                        .role(Role.USER)
                        .enabled(Boolean.TRUE)
                        .locked(Boolean.FALSE)
                        .build()
        );

        emailVerificationService.deleteVerificationCode(req.email());

        eventPublisher.publishEvent(userRegisteredEventFactory.from(userSaved, req));

        log.debug("User registered (DB saved), userId={}, email={}", userSaved.getId(), userSaved.getEmail());

        return userSaved;
    }
}