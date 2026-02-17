package com.lumina_bank.aiassistantservice.domain.intent.impl.user;

import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.dto.client.user.UserResponse;
import com.lumina_bank.aiassistantservice.domain.dto.client.user.UserUpdateDto;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.enums.ParamType;
import com.lumina_bank.aiassistantservice.domain.exception.ServiceCallException;
import com.lumina_bank.aiassistantservice.domain.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.domain.result.data.user.UserUpdatePreviewData;
import com.lumina_bank.aiassistantservice.domain.result.data.user.UserUpdatedData;
import com.lumina_bank.aiassistantservice.service.client.user.FeignUserGateway;
import com.lumina_bank.aiassistantservice.util.ParseDateUtil;
import com.lumina_bank.aiassistantservice.util.UserUpdateProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateUserInformationIntent implements IntentDefinition {
    private final FeignUserGateway userGateway;
    private final UserUpdateProcessor processor;

    @Override
    public Intent intent() {
        return Intent.UPDATE_USER_INFORMATION;
    }

    @Override
    public boolean requiresFinalConfirmation() {
        return true;
    }

    @Override
    public List<RequiredParam> requiredParams() {
        return List.of(
                new RequiredParam("firstName", ParamType.STRING, List.of(), "User first name."),
                new RequiredParam("lastName", ParamType.STRING, List.of(),"User last name."),
                new RequiredParam("email", ParamType.STRING, List.of(),"User email address."),
                new RequiredParam("phoneNumber", ParamType.STRING, List.of(),"User phone number."),
                new RequiredParam("birthDate", ParamType.DATE, List.of(),"Birth date"),
                new RequiredParam("street", ParamType.STRING, List.of(),"Street name."),
                new RequiredParam("city", ParamType.STRING, List.of(),"City name."),
                new RequiredParam("houseNumber", ParamType.STRING, List.of(),"House or building number."),
                new RequiredParam("zipCode", ParamType.STRING, List.of(),"Postal or ZIP code."),
                new RequiredParam("country", ParamType.STRING, List.of(),"Country name.")
        );
    }

    @Override
    public AssistantExecutionResult execute(Map<String, Object> params) {
        if (params.isEmpty()) {
            return AssistantExecutionResult.needClarification(
                    intent(),
                    new ClarificationData("NO_PARAMS_TO_CHANGE")
            );
        }

        try {
            UserResponse current = userGateway.getUser();

            if (params.containsKey("birthDate")) {
                if (ParseDateUtil.parseDate(params.get("birthDate")).isEmpty()) {
                    return AssistantExecutionResult.needClarification(
                            intent(),
                            new ClarificationData("INVALID_DATE_FORMAT")
                    );
                }
            }

            Map<String, Object> changes = processor.calculateChanges(current, params);

            if (changes.isEmpty()) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("VALUE_IS_THE_SAME")
                );
            }

            return AssistantExecutionResult.success(
                    intent(),
                    new UserUpdatePreviewData(changes)
            );

        } catch (ServiceCallException e) {
            return AssistantExecutionResult.error(
                    intent(),
                    e.getMessage()
            );
        }

    }

    @Override
    public AssistantExecutionResult perform(Map<String, Object> params) {
        try {
            UserResponse current = userGateway.getUser();

            UserUpdateDto dto = processor.merge(current, params);

            UserResponse updated = userGateway.updateUser(dto);

            return AssistantExecutionResult.success(
                    intent(),
                    new UserUpdatedData(updated)
            );

        } catch (ServiceCallException e) {
            return AssistantExecutionResult.error(
                    intent(),
                    e.getMessage());
        }
    }
}
