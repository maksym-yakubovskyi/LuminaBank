package com.lumina_bank.gatewayservice.config;

import com.lumina_bank.gatewayservice.util.SecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final SecurityProperties securityProperties;

    @Bean
    public SecurityWebFilterChain securityFilterChain(
            ServerHttpSecurity http, ReactiveJwtDecoder jwtDecoder
    ) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(Customizer.withDefaults())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(
                                "/api/auth/**",
                                "/actuator/**"
                        ).permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(t -> t.jwtDecoder(jwtDecoder)));

        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder
                .withJwkSetUri(securityProperties.jwksUri())
                .build();
        decoder.setJwtValidator(
                JwtValidators.createDefaultWithIssuer(securityProperties.issuer())
        );
        return decoder;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));
        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type"
        ));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

//    @Bean
//    public JwtAuthenticationConverter jwtAuthenticationConverter() {
//
//        JwtGrantedAuthoritiesConverter rolesConverter =
//                new JwtGrantedAuthoritiesConverter();
//
//        rolesConverter.setAuthoritiesClaimName("roles");
//        rolesConverter.setAuthorityPrefix("");
//
//        JwtAuthenticationConverter converter =
//                new JwtAuthenticationConverter();
//
//        converter.setJwtGrantedAuthoritiesConverter(rolesConverter);
//        return converter;
//    }
    //        .authorizeHttpRequests(auth -> auth
//                .requestMatchers("/admin/**").hasRole("ADMIN")
//                .requestMatchers("/user/**").hasRole("USER")
//                .anyRequest().authenticated()
//        )
//        .oauth2ResourceServer(oauth -> oauth
//                .jwt(jwt -> jwt
//                        .jwtAuthenticationConverter(jwtAuthenticationConverter())
//                )
//        )

}
