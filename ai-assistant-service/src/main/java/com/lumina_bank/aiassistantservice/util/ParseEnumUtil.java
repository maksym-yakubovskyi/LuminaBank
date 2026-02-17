package com.lumina_bank.aiassistantservice.util;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class ParseEnumUtil {

    private ParseEnumUtil() {}

    public static <E extends Enum<E>> List<String> enumValues(Class<E> e) {
        return Arrays.stream(e.getEnumConstants())
                .map(Enum::name)
                .toList();
    }

    public static <E extends Enum<E>> Optional<E> parseEnumSafe(
            Class<E> enumClass,
            Object value
    ) {
        if (value == null) return Optional.empty();

        String normalized = value.toString().trim().toUpperCase();

        for (E constant : enumClass.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(normalized)) {
                return Optional.of(constant);
            }
        }

        return Optional.empty();
    }
}

