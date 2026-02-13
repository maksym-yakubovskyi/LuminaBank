package com.lumina_bank.aiassistantservice.domain.intent.impl.user;

import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.dto.client.user.UserResponse;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.exception.ExternalServiceException;
import com.lumina_bank.aiassistantservice.domain.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.result.data.user.UserInfoData;
import com.lumina_bank.aiassistantservice.service.client.user.FeignUserGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserInformationIntent implements IntentDefinition {

    private final FeignUserGateway userGateway;

    @Override
    public Intent intent() {
        return Intent.USER_INFORMATION;
    }

    @Override
    public List<RequiredParam> requiredParams() {
        return List.of();
    }

    @Override
    public AssistantExecutionResult execute(Map<String, Object> params) {
        try{
            UserResponse user = userGateway.getUser();

            return AssistantExecutionResult.success(
                    intent(),
                    new UserInfoData(user)
            );

        } catch (ExternalServiceException e) {
            return AssistantExecutionResult.error(
                    intent(),
                    "Не вдалося отримати дані користувача"
            );
        }
    }
}
