package com.lumina_bank.userservice.config.event;

import com.lumina_bank.common.dto.event.user_events.UserRegisteredEvent;
import com.lumina_bank.common.enums.user.UserType;
import com.lumina_bank.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class UserEventsConsumerConfig {
    private final UserService userService;

    @Bean
    public Consumer<UserRegisteredEvent> userRegisteredConsumer() {
        return event -> {
            if (event == null) {
                log.warn("Received null UserRegisteredEvent");
                return;
            }

            log.info("Received UserRegisteredEvent authUserId={}", event.authUserId());

            if (event.userType().equals(UserType.INDIVIDUAL_USER)){
                userService.createUser(event);
            }
        };
    }
}
