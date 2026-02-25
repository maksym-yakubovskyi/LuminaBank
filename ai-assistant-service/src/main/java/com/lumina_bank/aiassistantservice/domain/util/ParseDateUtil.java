package com.lumina_bank.aiassistantservice.domain.util;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public final class ParseDateUtil {
    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy")
    );

    private static final List<DateTimeFormatter> YEAR_MONTH_FORMATS = List.of(
            DateTimeFormatter.ofPattern("MM.yyyy"),
            DateTimeFormatter.ofPattern("MM-yyyy"),
            DateTimeFormatter.ofPattern("yyyy/MM"),
            DateTimeFormatter.ofPattern("MM/yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM")
    );

    private ParseDateUtil() {}

    public static Optional<LocalDate> parseDate(Object raw) {

        if (raw == null) return Optional.empty();

        String value = raw.toString().trim();

        for (DateTimeFormatter f : DATE_FORMATS) {
            try {
                return Optional.of(LocalDate.parse(value, f));
            } catch (Exception ignored) {}
        }

        return Optional.empty();
    }

    public static Optional<YearMonth> parseYearMonth(Object raw) {

        if (raw == null) return Optional.empty();

        String value = raw.toString().trim();

        for (DateTimeFormatter f : YEAR_MONTH_FORMATS) {
            try {
                return Optional.of(YearMonth.parse(value, f));
            } catch (Exception ignored) {}
        }

        return Optional.empty();
    }
}
