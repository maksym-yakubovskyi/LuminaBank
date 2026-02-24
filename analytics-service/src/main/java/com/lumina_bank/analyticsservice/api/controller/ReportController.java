package com.lumina_bank.analyticsservice.api.controller;

import com.lumina_bank.analyticsservice.api.request.report.DailyReportRequest;
import com.lumina_bank.analyticsservice.api.request.report.MonthlyReportRequest;
import com.lumina_bank.analyticsservice.api.response.report.ReportResponse;
import com.lumina_bank.analyticsservice.api.request.report.TransactionHistoryReportRequest;
import com.lumina_bank.analyticsservice.domain.model.Report;
import com.lumina_bank.analyticsservice.infrastructure.storage.FileStorage;
import com.lumina_bank.analyticsservice.application.service.ReportService;
import com.lumina_bank.analyticsservice.application.report.ReportTransactionService;
import com.lumina_bank.common.security.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/analytics/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportService reportService;
    private final ReportTransactionService  reportTransactionService;
    private final FileStorage storage;

    @PostMapping("/monthly")
    public ResponseEntity<?> createMonthlyReport(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody MonthlyReportRequest request
            ){
        Long userId = JwtUtils.extractUserId(jwt);

        ReportResponse response = reportService.createMonthlyReport(userId,request.accountId(),request.month());

        log.info("Successfully created monthly report");

        return ResponseEntity.accepted().body(response);
    }

    @PostMapping("/daily")
    public ResponseEntity<?> createDailyReport(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody DailyReportRequest request
    ) {
        Long userId = JwtUtils.extractUserId(jwt);

        ReportResponse response = reportService.createDailyActivityReport(
                userId,
                request.from(),
                request.to()
        );

        log.info("Successfully created daily report");

        return ResponseEntity.accepted().body(response);
    }

    @PostMapping("/transaction-history")
    public ResponseEntity<?> createTransactionHistoryReport(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody TransactionHistoryReportRequest request
    ){
        Long userId = JwtUtils.extractUserId(jwt);

        ReportResponse response = reportService.createTransactionHistoryReport(
                userId,
                request.from(),
                request.to()
        );

        log.info("Successfully created transaction report");

        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<?> downloadReport(@PathVariable UUID id){
        log.info("GET /analytics/reports/download - Received request to download report");

        Report report = reportTransactionService.getReport(id);

        Resource file = storage.load(report.getFilePath());

        log.info("Successfully downloaded report");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + report.getFileName() + "\"")
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition")
                .contentType(report.getContentType())
                .body(file);
    }

    @GetMapping("/my")
    public ResponseEntity<?> myReport(@AuthenticationPrincipal Jwt jwt){
        Long userId = JwtUtils.extractUserId(jwt);

        return ResponseEntity.accepted().body(reportTransactionService.getUsersReports(userId));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<?> getReportStatus(@PathVariable UUID id){
        log.info("GET /analytics/reports/status - Received request to get report");

        return ResponseEntity.ok().body(reportTransactionService.getReportStatus(id));
    }
}
