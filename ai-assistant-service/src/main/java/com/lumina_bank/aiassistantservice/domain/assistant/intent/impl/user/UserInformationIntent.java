package com.lumina_bank.aiassistantservice.domain.assistant.intent.impl.user;

import com.lumina_bank.aiassistantservice.domain.assistant.AssistantContext;
import com.lumina_bank.aiassistantservice.domain.assistant.RequiredParam;
import com.lumina_bank.aiassistantservice.infrastructure.external.user.dto.BusinessUserResponse;
import com.lumina_bank.aiassistantservice.infrastructure.external.user.dto.UserResponse;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.exception.ServiceCallException;
import com.lumina_bank.aiassistantservice.domain.assistant.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.assistant.result.data.user.BusinessUserInfoData;
import com.lumina_bank.aiassistantservice.domain.assistant.result.data.user.UserInfoData;
import com.lumina_bank.aiassistantservice.infrastructure.external.user.FeignUserGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    public List<RequiredParam> requiredParams(AssistantContext context) {
        return List.of();
    }

    @Override
    public AssistantExecutionResult execute(
            Map<String, Object> params,
            UUID conversationId,
            AssistantContext context
    ) {
        try{
            if(context.isBusiness()){
                BusinessUserResponse business = userGateway.getBusinessUser();
                return AssistantExecutionResult.success(
                        intent(),
                        new BusinessUserInfoData(business)
                );
            }

            UserResponse user = userGateway.getUser();

            return AssistantExecutionResult.success(
                    intent(),
                    new UserInfoData(user)
            );

        } catch (ServiceCallException e) {
            return AssistantExecutionResult.error(
                    intent(),
                    e.getMessage()
            );
        }
    }
}
