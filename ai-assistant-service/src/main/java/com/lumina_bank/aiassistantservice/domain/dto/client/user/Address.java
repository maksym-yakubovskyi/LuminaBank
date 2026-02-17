package com.lumina_bank.aiassistantservice.domain.dto.client.user;

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
