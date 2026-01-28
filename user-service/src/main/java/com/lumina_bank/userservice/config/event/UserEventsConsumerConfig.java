package com.lumina_bank.userservice.config.event;

import com.lumina_bank.common.dto.event.user_events.UserRegisteredEvent;
import com.lumina_bank.userservice.service.BusinessUserService;
import com.lumina_bank.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.lumina_bank.common.dto.event.user_events.IndividualUserRegisteredEvent;
import com.lumina_bank.common.dto.event.user_events.BusinessUserRegisteredEvent;
import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class UserEventsConsumerConfig {
    private final UserService userService;
    private final BusinessUserService businessUserService;

    @Bean
    public Consumer<UserRegisteredEvent> userRegisteredConsumer() {
        return event -> {
            if (event == null) {
                log.warn("Received null UserRegisteredEvent");
                return;
            }

            log.info("Received UserRegisteredEvent authUserId={}", event.authUserId());

            if (event instanceof IndividualUserRegisteredEvent e){
                userService.createUser(e);
                return;
            }
            if (event instanceof BusinessUserRegisteredEvent e){
                businessUserService.createUser(e);
            }
        };
    }
}