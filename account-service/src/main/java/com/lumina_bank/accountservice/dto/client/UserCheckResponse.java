package com.lumina_bank.accountservice.dto.client;

import com.lumina_bank.common.enums.user.UserType;

public record UserCheckResponse(
        Long id,
        boolean exists,
        boolean active,
        UserType userType
) {}