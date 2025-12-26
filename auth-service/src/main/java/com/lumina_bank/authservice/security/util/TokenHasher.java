package com.lumina_bank.authservice.security.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

@Component
public class TokenHasher {
    private static final String HASH_ALGORITHM = "HmacSHA256";
    private final SecretKeySpec keySpec;

    public TokenHasher(
            @Value("${app.security.refresh-token.hmac-secret}")
            String secret
    ) {
        this.keySpec = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                HASH_ALGORITHM
        );
    }

    public String hash(String token) {
        try {
            Mac mac = Mac.getInstance(HASH_ALGORITHM);
            mac.init(keySpec);
            byte[] rawHash = mac.doFinal(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(rawHash);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to hash refresh token", e
            );
        }
    }
}