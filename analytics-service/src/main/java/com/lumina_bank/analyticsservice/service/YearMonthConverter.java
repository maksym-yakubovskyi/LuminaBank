package com.lumina_bank.analyticsservice.service;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.YearMonth;

@Converter(autoApply = true)
public class YearMonthConverter implements AttributeConverter<YearMonth, String> {

    @Override
    public String convertToDatabaseColumn(YearMonth yearMonth) {
        return yearMonth != null ? yearMonth.toString() : "";
    }

    @Override
    public YearMonth convertToEntityAttribute(String string) {
        return string != null ? YearMonth.parse(string) : null;
    }
}
