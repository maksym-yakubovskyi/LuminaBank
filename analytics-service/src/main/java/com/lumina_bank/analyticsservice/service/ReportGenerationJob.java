package com.lumina_bank.analyticsservice.service;

import com.lumina_bank.analyticsservice.enums.ReportStatus;
import com.lumina_bank.analyticsservice.model.AnalyticsDailySummary;
import com.lumina_bank.analyticsservice.model.AnalyticsTransactionEvent;
import com.lumina_bank.analyticsservice.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportGenerationJob {
    private final ReportRepository reportRepository;
    private final AnalyticsService analyticsService;
    private final PdfGenerator pdfGenerator;
    private final CsvGenerator csvGenerator;
    private final FileStorage storage;

    @Async
    public void generateMonthlyReport(UUID reportId, Long userId, Long accountId, YearMonth month){
        try{
            updateStatus(reportId,ReportStatus.PROCESSING);

            System.out.println(reportId);
            System.out.println(month);

            var overview = analyticsService.getMonthlyOverview(userId, accountId, month);

            var categories = analyticsService.getCategoryExpenses(userId,month);

            var topRecipients = analyticsService.getTopRecipients(userId);

            System.out.println(overview);
            System.out.println(categories);
            System.out.println(topRecipients);

            byte[] pdf = pdfGenerator.generate(
                    overview,categories,topRecipients
            );

            String path = storage.save(reportId + ".pdf", pdf);

            reportRepository.findById(reportId).ifPresent(report -> {
                report.setStatus(ReportStatus.READY);
                report.setFilePath(path);
                report.setCompletedAt(LocalDateTime.now());
                reportRepository.save(report);
            });
        }catch (Exception e){
            log.warn("Failed to generate report {}", reportId, e);
            updateStatus(reportId,ReportStatus.FAILED);
        }
    }

    @Async
    public void generateDailyActivityReport(
            UUID reportId,
            Long userId,
            LocalDate from,
            LocalDate to) {
        try{
            updateStatus(reportId,ReportStatus.PROCESSING);

            System.out.println(reportId);
            System.out.println(from);
            System.out.println(to);

            List<AnalyticsDailySummary> summaries = analyticsService.getDailySummaries(userId, from, to);

            System.out.println(summaries);

            byte[] csv = csvGenerator.generateDailyActivity(summaries);
            String path = storage.save(reportId + ".csv", csv);
            reportRepository.findById(reportId).ifPresent(report -> {
                report.setStatus(ReportStatus.READY);
                report.setFilePath(path);
                report.setCompletedAt(LocalDateTime.now());
                reportRepository.save(report);
            });
        }catch (Exception e){
            log.warn("Failed to generate report {}", reportId, e);
            updateStatus(reportId,ReportStatus.FAILED);
        }
    }

    @Async
    public void generateTransactionHistoryReport(
            UUID reportId,
            Long userId,
            LocalDate from,
            LocalDate to
    ){
        try{
            updateStatus(reportId,ReportStatus.PROCESSING);

            List<AnalyticsTransactionEvent> summaries = analyticsService.getTransactionHistory(userId, from, to);

            byte[] csv = csvGenerator.generateTransactionHistory(summaries);
            String path = storage.save(reportId + ".csv", csv);
            reportRepository.findById(reportId).ifPresent(report -> {
                report.setStatus(ReportStatus.READY);
                report.setFilePath(path);
                report.setCompletedAt(LocalDateTime.now());
                reportRepository.save(report);
            });
        }catch (Exception e){
            log.warn("Failed to generate report {}", reportId, e);
            updateStatus(reportId,ReportStatus.FAILED);
        }
    }

    private void updateStatus(UUID reportId, ReportStatus status){
        reportRepository.findById(reportId).ifPresent(report -> {
            report.setStatus(status);
            reportRepository.save(report);
        });
    }
}
