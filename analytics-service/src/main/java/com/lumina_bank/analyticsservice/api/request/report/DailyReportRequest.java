package com.lumina_bank.analyticsservice.api.request.report;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DailyReportRequest(
        @NotNull LocalDate from,
        @NotNull LocalDate to
) {
    @AssertTrue
    public boolean isValidRange() {
        return from == null || to == null || !from.isAfter(to);
    }
}
