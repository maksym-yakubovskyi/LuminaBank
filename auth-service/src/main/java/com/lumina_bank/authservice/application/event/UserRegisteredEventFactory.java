package com.lumina_bank.authservice.application.event;

import com.lumina_bank.authservice.api.request.RegisterBusinessUserRequest;
import com.lumina_bank.authservice.api.request.RegisterUserRequest;
import com.lumina_bank.authservice.domain.model.User;
import com.lumina_bank.common.dto.event.user_events.BusinessUserRegisteredEvent;
import com.lumina_bank.common.dto.event.user_events.IndividualUserRegisteredEvent;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredEventFactory {
    public IndividualUserRegisteredEvent from(User user, RegisterUserRequest req) {
        return new IndividualUserRegisteredEvent(
                user.getId(),
                user.getEmail(),
                req.phoneNumber(),
                req.userType(),
                user.getCreatedAt(),
                req.firstName(),
                req.lastName(),
                req.birthDate()
        );
    }

    public BusinessUserRegisteredEvent from(User user, RegisterBusinessUserRequest req) {
        return new BusinessUserRegisteredEvent(
                user.getId(),
                user.getEmail(),
                req.phoneNumber(),
                req.userType(),
                user.getCreatedAt(),
                req.companyName(),
                req.adrpou(),
                req.category()
        );
    }
}