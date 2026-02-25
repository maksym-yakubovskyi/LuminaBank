package com.lumina_bank.aiassistantservice.infrastructure.external.user.dto;

public record Address(
        String street,
        String city,
        String houseNumber,
        String zipCode,
        String country
) {
    public Address() {
        this(null, null, null, null, null);
    }
}
