package com.lumina_bank.authservice.service.event;

import com.lumina_bank.common.dto.event.user_events.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventsPublisher {
    private final StreamBridge streamBridge;

    public void publishUserRegistered(UserRegisteredEvent event){
        log.info("Publishing UserRegistered event to stream");

        boolean sent = streamBridge.send("userRegistered-out-0", event);

        if(sent){
            log.info("UserRegistered event sent successfully, authUserId={}",event.authUserId());
        }else {
            log.warn("Failed to publish UserRegisteredEvent authUserId={}",event.authUserId());
        }
    }
}
