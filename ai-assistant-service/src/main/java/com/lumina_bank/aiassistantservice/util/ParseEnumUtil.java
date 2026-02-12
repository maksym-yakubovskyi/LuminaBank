package com.lumina_bank.aiassistantservice.util;

import java.util.Arrays;
import java.util.List;

public class ParseEnumUtil {

    public static <E extends Enum<E>> List<String> enumValues(Class<E> e) {
        return Arrays.stream(e.getEnumConstants())
                .map(Enum::name)
                .toList();
    }

    public static <E extends Enum<E>> E parseEnum(
            Class<E> enumClass,
            Object value
    ) {
        try {
            return Enum.valueOf(enumClass, value.toString());
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Invalid value for " + enumClass.getSimpleName()
            );
        }
    }
}
