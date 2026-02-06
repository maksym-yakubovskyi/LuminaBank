package com.lumina_bank.analyticsservice.dto.report;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TransactionHistoryReportRequest(
        @NotNull LocalDate from,
        @NotNull LocalDate to
) {
    @AssertTrue(message = "from must be before or equal to to")
    public boolean isValidRange() {
        return from == null || to == null || !from.isAfter(to);
    }
}
