package com.lumina_bank.authservice.security.util;

import java.security.SecureRandom;
import java.util.Base64;

public final class RefreshTokenGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int NUMBER_OF_BYTES = 32;

    private RefreshTokenGenerator() {}

    public static String generate() {
        byte[] bytes = new byte[NUMBER_OF_BYTES];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
