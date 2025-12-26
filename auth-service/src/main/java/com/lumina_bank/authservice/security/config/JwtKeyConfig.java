package com.lumina_bank.authservice.security.config;

import com.nimbusds.jose.JOSEException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.UUID;

@Configuration
@Slf4j
public class JwtKeyConfig {

    /**
     * TODO:
     * 1. Для розробки (dev) можна генерувати ключ на старті, як зараз.
     * 2. Для продакшена ключ має бути стабільним та постійним:
     *    - Не генерувати на старті, інакше після рестарту всі JWT стануть невалідними.
     *    - Постачати ключ через:
     *        • Java Keystore (JKS / PKCS12)
     *        • Vault / KMS
     *        • або як зашифрований secret у середовищі (env variable)
     * 3. Можна зробити умовну логіку: якщо dev → генеруємо динамічно, якщо prod → беремо з безпечного джерела.
     * 4. Перевірити сумісність ключа з JwtEncoder / JwtDecoder.
     */

    @Bean
    public KeyPair keyPair() {
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(2048);
            return gen.generateKeyPair();
        } catch (Exception e) {
            log.warn("KeyPair generation failed: ", e);

            throw new IllegalStateException("Failed to generate RSA key pair: ", e);
        }
    }

    @Bean
    public RSAKey rsaKey(KeyPair keyPair) {
        return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey((RSAPrivateKey) keyPair.getPrivate())
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    @Bean
    public JWKSet jwkSet(RSAKey rsaKey) {
        return new JWKSet(rsaKey);
    }

    @Bean
    public JwtEncoder jwtEncoder(RSAKey rsaKey) {
        var jwtSource = new ImmutableJWKSet<>(new JWKSet(rsaKey));
        return new NimbusJwtEncoder(jwtSource);
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAKey rsaKey) throws JOSEException {
        return NimbusJwtDecoder
                .withPublicKey(rsaKey.toRSAPublicKey())
                .build();
    }
}
