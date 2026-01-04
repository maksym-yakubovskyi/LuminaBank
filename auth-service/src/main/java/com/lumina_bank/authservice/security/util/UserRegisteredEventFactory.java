package com.lumina_bank.authservice.security.util;

import com.lumina_bank.authservice.dto.RegisterUserRequest;
import com.lumina_bank.authservice.model.User;
import com.lumina_bank.common.dto.event.user_events.UserRegisteredEvent;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class UserRegisteredEventFactory {
    public UserRegisteredEvent from(User user, RegisterUserRequest req){
        return new UserRegisteredEvent(
                req.firstName(),
                req.lastName(),
                user.getEmail(),
                req.phoneNumber(),
                req.birthDate(),
                req.userType(),
                user.getCreatedAt() != null ? user.getCreatedAt() : Instant.now(),
                user.getId()
        );
    }
}
