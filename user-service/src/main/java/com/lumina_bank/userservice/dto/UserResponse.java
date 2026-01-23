package com.lumina_bank.userservice.dto;

import com.lumina_bank.common.enums.user.Role;
import com.lumina_bank.userservice.model.Address;
import com.lumina_bank.userservice.model.User;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        LocalDate birthDate,
        Address address,
        String role
) {
    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .birthDate(user.getBirthDate())
                .address(user.getAddress())
                .role(user.getRole().name())
                .build();
    }
}
