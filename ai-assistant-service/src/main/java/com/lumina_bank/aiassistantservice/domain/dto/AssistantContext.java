package com.lumina_bank.aiassistantservice.domain.dto;

import com.lumina_bank.common.enums.user.Role;

public record AssistantContext(
        Long userId,
        Role role
) {
    public boolean isBusiness() {
        return role == Role.BUSINESS_USER;
    }
    public boolean isIndividual() {
        return role == Role.INDIVIDUAL_USER;
    }
    public boolean isManager() {
        return role == Role.MANAGER;
    }
}
