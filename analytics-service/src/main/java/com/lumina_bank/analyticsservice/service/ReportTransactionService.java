package com.lumina_bank.analyticsservice.service;

import com.lumina_bank.analyticsservice.dto.report.ReportResponse;
import com.lumina_bank.analyticsservice.enums.ReportFormat;
import com.lumina_bank.analyticsservice.enums.ReportStatus;
import com.lumina_bank.analyticsservice.enums.ReportType;
import com.lumina_bank.analyticsservice.model.Report;
import com.lumina_bank.analyticsservice.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportTransactionService {
    private final ReportRepository reportRepository;

    @Transactional
    public void saveReport(
            UUID id,
            Long userId,
            ReportType reportType,
            ReportFormat format,
            ReportStatus status,
            String filename,
            MediaType mediaType){
        reportRepository.save(
                Report.builder()
                        .id(id)
                        .userId(userId)
                        .reportType(reportType)
                        .format(format)
                        .status(status)
                        .fileName(filename)
                        .contentType(mediaType)
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public Report getReport(UUID reportId){
        return reportRepository.findByIdAndStatus(reportId,ReportStatus.READY).orElseThrow(() ->new RuntimeException("Report not ready"));
    }

    @Transactional(readOnly = true)
    public ReportStatus getReportStatus(UUID reportId){
        return reportRepository.findById(reportId)
                .orElseThrow(() ->new RuntimeException("Report not ready")).getStatus();
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> getUsersReports(Long userId){
        return reportRepository.findAllByUserId(userId).stream()
                .map(ReportResponse::from)
                .toList();
    }
}
