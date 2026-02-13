package com.lumina_bank.aiassistantservice.domain.intent.impl.user;

import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.dto.client.user.Address;
import com.lumina_bank.aiassistantservice.domain.dto.client.user.UserResponse;
import com.lumina_bank.aiassistantservice.domain.dto.client.user.UserUpdateDto;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.enums.ParamType;
import com.lumina_bank.aiassistantservice.domain.exception.ExternalServiceException;
import com.lumina_bank.aiassistantservice.domain.intent.IntentDefinition;
import com.lumina_bank.aiassistantservice.domain.result.AssistantExecutionResult;
import com.lumina_bank.aiassistantservice.domain.result.data.ClarificationData;
import com.lumina_bank.aiassistantservice.domain.result.data.user.UserUpdatePreviewData;
import com.lumina_bank.aiassistantservice.domain.result.data.user.UserUpdatedData;
import com.lumina_bank.aiassistantservice.service.client.user.FeignUserGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateUserInformationIntent implements IntentDefinition {
    private final FeignUserGateway userGateway;

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
                new RequiredParam(
                        "firstName",
                        ParamType.STRING,
                        List.of()
                ),
                new RequiredParam(
                        "lastName",
                        ParamType.STRING,
                        List.of()
                ),
                new RequiredParam(
                        "email",
                        ParamType.STRING,
                        List.of()
                ),
                new RequiredParam(
                        "phoneNumber",
                        ParamType.STRING,
                        List.of()
                ),
                new RequiredParam(
                        "birthDate",
                        ParamType.DATE,
                        List.of()
                ),
                new RequiredParam(
                        "street",
                        ParamType.STRING,
                        List.of()
                ),
                new RequiredParam(
                        "city",
                        ParamType.STRING,
                        List.of()
                ),
                new RequiredParam(
                        "houseNumber",
                        ParamType.STRING,
                        List.of()
                ),
                new RequiredParam(
                        "zipCode",
                        ParamType.STRING,
                        List.of()
                ),
                new RequiredParam(
                        "country",
                        ParamType.STRING,
                        List.of()
                )
        );
    }

    @Override
    public AssistantExecutionResult execute(Map<String, Object> params) {

        if (params.isEmpty()) {
            return AssistantExecutionResult.needClarification(
                    intent(),
                    new ClarificationData("Що саме потрібно змінити?")
            );
        }

        try {
            UserResponse current = userGateway.getUser();

            Map<String, Object> changes = calculateChanges(current, params);

            if (changes.isEmpty()) {
                return AssistantExecutionResult.needClarification(
                        intent(),
                        new ClarificationData("Нові дані не відрізняються від поточних.")
                );
            }

            return AssistantExecutionResult.success(
                    intent(),
                    new UserUpdatePreviewData(changes)
            );

        } catch (ExternalServiceException e) {
            return AssistantExecutionResult.error(intent(),
                    "Не вдалося отримати профіль користувача");
        }

    }

    @Override
    public AssistantExecutionResult perform(Map<String, Object> params) {
        try {
            UserResponse current = userGateway.getUser();

            UserUpdateDto dto = mergeIntoDto(current, params);

            UserResponse updated = userGateway.updateUser(dto);

            return AssistantExecutionResult.success(
                    intent(),
                    new UserUpdatedData(updated)
            );

        } catch (ExternalServiceException e) {
            return AssistantExecutionResult.error(intent(),
                    e.getMessage());
        }
    }

    private Map<String,Object> calculateChanges(UserResponse current, Map<String,Object> params) {

        Map<String,Object> changes = new LinkedHashMap<>();

        params.forEach((k,v) -> {
            Object oldValue = extractCurrentValue(current, k);

            if (oldValue != null && !oldValue.equals(v)) {
                changes.put(k, Map.of(
                        "old", oldValue,
                        "new", v
                ));
            }
        });

        return changes;
    }

    private Object extractCurrentValue(UserResponse current, String field) {

        if (current == null) return null;

        return switch (field) {

            case "firstName"   -> current.firstName();
            case "lastName"    -> current.lastName();
            case "email"       -> current.email();
            case "phoneNumber" -> current.phoneNumber();
            case "birthDate"   -> current.birthDate();

            case "street" ->
                    current.address() != null ? current.address().street() : null;

            case "city" ->
                    current.address() != null ? current.address().city() : null;

            case "houseNumber" ->
                    current.address() != null ? current.address().houseNumber() : null;

            case "zipCode" ->
                    current.address() != null ? current.address().zipCode() : null;

            case "country" ->
                    current.address() != null ? current.address().country() : null;

            default -> null;
        };
    }

    private UserUpdateDto mergeIntoDto(UserResponse current, Map<String, Object> params) {

        Address address = current.address();

        if (address == null) {
            address = new Address();
        }

        return new UserUpdateDto(

                // --- BASIC INFO ---
                getString(params, "firstName", current.firstName()),
                getString(params, "lastName", current.lastName()),
                getString(params, "email", current.email()),
                getString(params, "phoneNumber", current.phoneNumber()),

                // --- DATE ---
                params.containsKey("birthDate")
                        ? parseDate(params.get("birthDate"))
                        : current.birthDate(),

                // --- ADDRESS ---
                getString(params, "street", address.street()),
                getString(params, "city", address.city()),
                getString(params, "houseNumber", address.houseNumber()),
                getString(params, "zipCode", address.zipCode()),
                getString(params, "country", address.country())
        );
    }

    private String getString(Map<String, Object> params, String key, String fallback) {
        Object value = params.get(key);
        return value != null ? value.toString().trim() : fallback;
    }

    private LocalDate parseDate(Object raw) {

        if (raw == null) return null;

        String value = raw.toString().trim();

        List<DateTimeFormatter> formats = List.of(
                DateTimeFormatter.ISO_LOCAL_DATE,          // 1995-05-10
                DateTimeFormatter.ofPattern("dd.MM.yyyy"), // 10.05.1995
                DateTimeFormatter.ofPattern("dd-MM-yyyy")  // 10-05-1995
        );

        for (DateTimeFormatter f : formats) {
            try {
                return LocalDate.parse(value, f);
            } catch (Exception ignored) {}
        }

        throw new ValidationException("Невірний формат дати. Використайте YYYY-MM-DD");
    }
}
