package com.lumina_bank.analyticsservice.application.analytics;

import com.lumina_bank.analyticsservice.infrastructure.external.user.UserServiceClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipientNameResolver {
    private final UserServiceClient userServiceClient;

    public String resolveRecipientName(Long recipientId){
        try{
            return userServiceClient.getBusinessUserNameById(recipientId);
        }catch (FeignException.NotFound ignored){}
        try {
            return userServiceClient.getUserNameById(recipientId);
        } catch (FeignException.NotFound ignored) {}

        return "Recipient #" + recipientId;
    }
}
