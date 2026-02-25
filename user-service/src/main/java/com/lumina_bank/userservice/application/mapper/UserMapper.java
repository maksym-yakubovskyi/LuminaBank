package com.lumina_bank.userservice.application.mapper;

import com.lumina_bank.userservice.api.response.UserResponse;
import com.lumina_bank.userservice.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .birthDate(user.getBirthDate())
                .address(user.getAddress())
                .role(user.getRole())
                .build();
    }
}
