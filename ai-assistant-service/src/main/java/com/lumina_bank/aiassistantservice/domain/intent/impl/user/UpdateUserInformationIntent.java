package com.lumina_bank.aiassistantservice.domain.intent.impl.user;

import com.lumina_bank.aiassistantservice.domain.dto.AssistantContext;
import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.dto.client.user.BusinessUserResponse;
import com.lumina_bank.aiassistantservice.domain.dto.client.user.BusinessUserUpdateDto;
import com.lumina_bank.aiassistantservice.domain.dto.client.user.UserResponse;
import com.lumina_bank.aiassistantservice.domain.dto.client.user.UserUpdateDto;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.enums.ParamType;
import com.lumina_bank.aiassistantservice.domain.exception.ServiceCallException;
import com.lumina_bank.aiassistantservice.domain.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.domain.result.data.user.BusinessUserUpdatePreviewData;
import com.lumina_bank.aiassistantservice.domain.result.data.user.BusinessUserUpdatedData;
import com.lumina_bank.aiassistantservice.domain.result.data.user.UserUpdatePreviewData;
import com.lumina_bank.aiassistantservice.domain.result.data.user.UserUpdatedData;
import com.lumina_bank.aiassistantservice.service.client.user.FeignUserGateway;
import com.lumina_bank.aiassistantservice.util.BusinessUserUpdateProcessor;
import com.lumina_bank.aiassistantservice.util.ParseDateUtil;
import com.lumina_bank.aiassistantservice.util.ParseEnumUtil;
import com.lumina_bank.aiassistantservice.util.UserUpdateProcessor;
import com.lumina_bank.common.enums.user.BusinessCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateUserInformationIntent implements IntentDefinition {
    private final FeignUserGateway userGateway;
    private final UserUpdateProcessor processorU;
    private final BusinessUserUpdateProcessor processorB;

    @Override
    public Intent intent() {
        return Intent.UPDATE_USER_INFORMATION;
    }

    @Override
    public boolean requiresFinalConfirmation() {
        return true;
    }

    @Override
    public List<RequiredParam> requiredParams(AssistantContext context) {

        if (context.isBusiness()) {
            return businessParams();
        }

        return individualParams();
    }
    private List<RequiredParam> individualParams() {
        return List.of(
                new RequiredParam("firstName", ParamType.STRING, List.of(), "User first name."),
                new RequiredParam("lastName", ParamType.STRING, List.of(),"User last name."),
                new RequiredParam("email", ParamType.STRING, List.of(),"User email address."),
                new RequiredParam("phoneNumber", ParamType.STRING, List.of(),"User phone number."),
                new RequiredParam("birthDate", ParamType.DATE, List.of(),"Birth date"),
                new RequiredParam("street", ParamType.STRING, List.of(),"Street name."),
                new RequiredParam("city", ParamType.STRING, List.of(),"City name."),
                new RequiredParam("houseNumber", ParamType.STRING, List.of(),"House number."),
                new RequiredParam("zipCode", ParamType.STRING, List.of(),"Postal code."),
                new RequiredParam("country", ParamType.STRING, List.of(),"Country name.")
        );
    }
    private List<RequiredParam> businessParams() {
        return List.of(
                new RequiredParam("companyName", ParamType.STRING, List.of(),"Company name."),
                new RequiredParam("email", ParamType.STRING, List.of(),"Business email."),
                new RequiredParam("phoneNumber", ParamType.STRING, List.of(),"Business phone."),
                new RequiredParam("adrpou", ParamType.STRING, List.of(),"EDRPOU code."),
                new RequiredParam("description", ParamType.STRING, List.of(),"Company description."),
                new RequiredParam("category", ParamType.ENUM, ParseEnumUtil.enumValues(BusinessCategory.class),"Business category."),
                new RequiredParam("street", ParamType.STRING, List.of(),"Street."),
                new RequiredParam("city", ParamType.STRING, List.of(),"City."),
                new RequiredParam("houseNumber", ParamType.STRING, List.of(),"Building number."),
                new RequiredParam("zipCode", ParamType.STRING, List.of(),"ZIP code."),
                new RequiredParam("country", ParamType.STRING, List.of(),"Country.")
        );
    }

    @Override
    public AssistantExecutionResult execute(
            Map<String, Object> params,
            UUID conversationId,
            AssistantContext context
    ) {
        if (params.isEmpty()) {
            return AssistantExecutionResult.needClarification(
                    intent(),
                    new ClarificationData("NO_PARAMS_TO_CHANGE")
            );
        }

        try {
            if (context.isBusiness()) {
                return handleBusinessPreview(params);
            }

            return handleUserPreview(params);

        } catch (ServiceCallException e) {
            return AssistantExecutionResult.error(intent(), e.getMessage());
        }
    }

    @Override
    public AssistantExecutionResult perform(
            Map<String, Object> params,
            AssistantContext context
    ) {
        try {
            if (context.isBusiness()) {
                return performBusinessUpdate(params);
            }

            return performUserUpdate(params);

        } catch (ServiceCallException e) {
            return AssistantExecutionResult.error(intent(), e.getMessage());
        }
    }

    private AssistantExecutionResult handleUserPreview(Map<String, Object> params) {
        UserResponse current = userGateway.getUser();

        if (params.containsKey("birthDate")) {
            if (ParseDateUtil.parseDate(params.get("birthDate")).isEmpty()) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("INVALID_DATE_FORMAT")
                );
            }
        }

        Map<String, Object> changes =
                processorU.calculateChanges(current, params);

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
    }

    private AssistantExecutionResult handleBusinessPreview(Map<String, Object> params) {

        BusinessUserResponse current = userGateway.getBusinessUser();

        Map<String, Object> changes =
                processorB.calculateChanges(current, params);

        if (changes.isEmpty()) {
            return AssistantExecutionResult.needClarification(
                    intent(),
                    new ClarificationData("VALUE_IS_THE_SAME")
            );
        }

        return AssistantExecutionResult.success(
                intent(),
                new BusinessUserUpdatePreviewData(changes)
        );
    }

    private AssistantExecutionResult performUserUpdate(Map<String, Object> params) {

        UserResponse current = userGateway.getUser();

        UserUpdateDto dto = processorU.merge(current, params);

        UserResponse updated = userGateway.updateUser(dto);

        return AssistantExecutionResult.success(
                intent(),
                new UserUpdatedData(updated)
        );
    }

    private AssistantExecutionResult performBusinessUpdate(Map<String, Object> params) {

        BusinessUserResponse current = userGateway.getBusinessUser();

        BusinessUserUpdateDto dto =
                processorB.merge(current, params);

        BusinessUserResponse updated =
                userGateway.updateBusinessUser(dto);

        return AssistantExecutionResult.success(
                intent(),
                new BusinessUserUpdatedData(updated)
        );
    }


}
