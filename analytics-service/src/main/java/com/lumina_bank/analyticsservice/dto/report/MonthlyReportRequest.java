package com.lumina_bank.analyticsservice.dto.report;

import jakarta.validation.constraints.NotNull;

import java.time.YearMonth;

public record   MonthlyReportRequest(
        @NotNull Long accountId,
        @NotNull YearMonth month
) {
}
