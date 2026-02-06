package com.lumina_bank.analyticsservice.dto.report;

import com.lumina_bank.analyticsservice.enums.ReportStatus;
import com.lumina_bank.analyticsservice.enums.ReportType;
import com.lumina_bank.analyticsservice.model.Report;

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
