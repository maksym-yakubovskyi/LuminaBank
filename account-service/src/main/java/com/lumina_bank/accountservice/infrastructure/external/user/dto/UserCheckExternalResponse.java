package com.lumina_bank.accountservice.infrastructure.external.user.dto;

import com.lumina_bank.common.enums.user.UserType;

public record UserCheckExternalResponse(
        Long id,
        boolean exists,
        boolean active,
        UserType userType
) {}