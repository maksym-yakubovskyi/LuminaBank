package com.lumina_bank.userservice.dto;

import com.lumina_bank.common.enums.user.UserType;

public record UserCheckResponse(
        Long userId,
        boolean exists,
        boolean active,
        UserType userType
) {}