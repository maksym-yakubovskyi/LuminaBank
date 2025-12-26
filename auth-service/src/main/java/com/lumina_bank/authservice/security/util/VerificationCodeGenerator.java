package com.lumina_bank.authservice.security.util;

import java.security.SecureRandom;

public final class VerificationCodeGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int CODE_LENGTH = 6;

    private VerificationCodeGenerator(){}

    public static String generateVerificationCode(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(secureRandom.nextInt(10));
        }
        return sb.toString();
    }
}
