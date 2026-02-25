package com.lumina_bank.analyticsservice.api.response.report;

import com.lumina_bank.analyticsservice.domain.enums.ReportStatus;
import com.lumina_bank.analyticsservice.domain.enums.ReportType;
import com.lumina_bank.analyticsservice.domain.model.Report;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReportResponse(
        UUID id,
        ReportType reportType,
        LocalDateTime createdAt,
        ReportStatus status
) {

    public static ReportResponse from(Report report){
        return new ReportResponse(
                report.getId(),
                report.getReportType(),
                report.getCreatedAt(),
                report.getStatus()
        );
    }
}
