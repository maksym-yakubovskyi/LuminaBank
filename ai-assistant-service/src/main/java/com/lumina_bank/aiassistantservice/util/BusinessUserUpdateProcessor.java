package com.lumina_bank.aiassistantservice.util;
import com.lumina_bank.aiassistantservice.domain.dto.client.user.Address;
import com.lumina_bank.aiassistantservice.domain.dto.client.user.BusinessUserResponse;
import com.lumina_bank.aiassistantservice.domain.dto.client.user.BusinessUserUpdateDto;
import com.lumina_bank.common.enums.user.BusinessCategory;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class BusinessUserUpdateProcessor {

    private static final Map<String, Function<BusinessUserResponse, Object>> FIELD_EXTRACTORS =
            Map.of(
                    "companyName", BusinessUserResponse::companyName,
                    "email", BusinessUserResponse::email,
                    "phoneNumber", BusinessUserResponse::phoneNumber,
                    "adrpou", BusinessUserResponse::adrpou,
                    "description", BusinessUserResponse::description,
                    "category", BusinessUserResponse::category
            );

    public Map<String, Object> calculateChanges(
            BusinessUserResponse current,
            Map<String, Object> params
    ) {

        Map<String, Object> changes = new LinkedHashMap<>();

        params.forEach((k, v) -> {
            Object oldValue = extractCurrentValue(current, k);

            if (oldValue != null && !oldValue.equals(v)) {
                changes.put(k, Map.of("old", oldValue, "new", v));
            }
        });

        return changes;
    }

    public BusinessUserUpdateDto merge(
            BusinessUserResponse current,
            Map<String, Object> params
    ) {

        Address address = current.address() != null
                ? current.address()
                : new Address();

        return new BusinessUserUpdateDto(
                getString(params, "email", current.email()),
                getString(params, "phoneNumber", current.phoneNumber()),
                getString(params, "companyName", current.companyName()),
                getString(params, "adrpou", current.adrpou()),
                getString(params, "description", current.description()),
                parseCategory(params, current.category()),
                getString(params, "street", address.street()),
                getString(params, "city", address.city()),
                getString(params, "houseNumber", address.houseNumber()),
                getString(params, "zipCode", address.zipCode()),
                getString(params, "country", address.country())
        );
    }

    private BusinessCategory parseCategory(
            Map<String, Object> params,
            BusinessCategory fallback
    ) {
        return ParseEnumUtil.parseEnumSafe(
                BusinessCategory.class,
                params.get("category")
        ).orElse(fallback);
    }

    private Object extractCurrentValue(BusinessUserResponse current, String field) {
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
