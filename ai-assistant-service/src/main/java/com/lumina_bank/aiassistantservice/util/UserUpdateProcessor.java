package com.lumina_bank.aiassistantservice.util;

import com.lumina_bank.aiassistantservice.domain.dto.client.user.Address;
import com.lumina_bank.aiassistantservice.domain.dto.client.user.UserResponse;
import com.lumina_bank.aiassistantservice.domain.dto.client.user.UserUpdateDto;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class UserUpdateProcessor {

    private static final Map<String, Function<UserResponse, Object>> FIELD_EXTRACTORS =
            Map.of(
                    "firstName", UserResponse::firstName,
                    "lastName", UserResponse::lastName,
                    "email", UserResponse::email,
                    "phoneNumber", UserResponse::phoneNumber,
                    "birthDate", UserResponse::birthDate
            );

    public Map<String, Object> calculateChanges(
            UserResponse current,
            Map<String, Object> params
    ) {

        Map<String, Object> changes = new LinkedHashMap<>();

        params.forEach((k, v) -> {
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

    public UserUpdateDto merge(
            UserResponse current,
            Map<String, Object> params
    ) {

        Address address = current.address() != null
                ? current.address()
                : new Address();

        return new UserUpdateDto(
                getString(params, "firstName", current.firstName()),
                getString(params, "lastName", current.lastName()),
                getString(params, "email", current.email()),
                getString(params, "phoneNumber", current.phoneNumber()),
                ParseDateUtil.parseDate(params.get("birthDate"))
                        .orElse(current.birthDate()),
                getString(params, "street", address.street()),
                getString(params, "city", address.city()),
                getString(params, "houseNumber", address.houseNumber()),
                getString(params, "zipCode", address.zipCode()),
                getString(params, "country", address.country())
        );
    }

    private Object extractCurrentValue(UserResponse current, String field) {

        if (current == null) return null;

        if (FIELD_EXTRACTORS.containsKey(field)) {
            return FIELD_EXTRACTORS.get(field).apply(current);
        }

        if (current.address() == null) return null;

        return switch (field) {
            case "street" -> current.address().street();
            case "city" -> current.address().city();
            case "houseNumber" -> current.address().houseNumber();
            case "zipCode" -> current.address().zipCode();
            case "country" -> current.address().country();
            default -> null;
        };
    }

    private String getString(Map<String, Object> params, String key, String fallback) {
        Object value = params.get(key);
        return value != null ? value.toString().trim() : fallback;
    }
}
