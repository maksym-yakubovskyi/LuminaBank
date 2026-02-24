package com.lumina_bank.userservice.api.response;

import com.lumina_bank.common.enums.user.Role;
import com.lumina_bank.userservice.domain.model.Address;
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
        Role role
) {}
