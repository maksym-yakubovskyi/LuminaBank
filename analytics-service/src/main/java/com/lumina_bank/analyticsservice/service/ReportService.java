package com.lumina_bank.analyticsservice.service;

import com.lumina_bank.analyticsservice.dto.report.ReportResponse;
import com.lumina_bank.analyticsservice.enums.ReportFormat;
import com.lumina_bank.analyticsservice.enums.ReportStatus;
import com.lumina_bank.analyticsservice.enums.ReportType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    private final ReportTransactionService reportTransactionService;
    private final ReportGenerationJob generationJob;

    public ReportResponse createMonthlyReport(Long userId, Long accountId, YearMonth month){
        UUID id = UUID.randomUUID();

        reportTransactionService.saveReport(
                id,
                userId,
                ReportType.MONTHLY_FINANCIAL,
                ReportFormat.PDF,
                ReportStatus.PENDING,
                "monthly-report.pdf",
                MediaType.APPLICATION_PDF
        );

        generationJob.generateMonthlyReport(id,userId,accountId,month);

        return new ReportResponse(id,ReportType.MONTHLY_FINANCIAL, LocalDateTime.now(),ReportStatus.PENDING);
    }

    public ReportResponse createDailyActivityReport(
            Long userId,
            LocalDate from,
            LocalDate to
    ) {
        UUID id = UUID.randomUUID();

        reportTransactionService.saveReport(
                id,
                userId,
                ReportType.DAILY_ACTIVITY,
                ReportFormat.CSV,
                ReportStatus.PENDING,
                "daily-activity.csv",
                MediaType.TEXT_PLAIN
        );

        generationJob.generateDailyActivityReport(id, userId, from, to);
        return  new ReportResponse(id,ReportType.DAILY_ACTIVITY, LocalDateTime.now(),ReportStatus.PENDING);
    }

    public ReportResponse createTransactionHistoryReport(
            Long userId,
            LocalDate from,
            LocalDate to
    ) {
        UUID id = UUID.randomUUID();

        reportTransactionService.saveReport(
                id,
                userId,
                ReportType.TRANSACTION_HISTORY,
                ReportFormat.CSV,
                ReportStatus.PENDING,
                "transaction-history.csv",
                MediaType.TEXT_PLAIN
        );

        generationJob.generateTransactionHistoryReport(id, userId, from, to);
        return new ReportResponse(id,ReportType.TRANSACTION_HISTORY, LocalDateTime.now(),ReportStatus.PENDING);
    }
}
