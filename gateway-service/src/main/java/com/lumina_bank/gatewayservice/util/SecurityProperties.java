package com.lumina_bank.gatewayservice.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.jwt")
public record SecurityProperties(
        String issuer,
        String jwksUri
) {
}