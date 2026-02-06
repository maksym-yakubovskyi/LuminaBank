package com.lumina_bank.analyticsservice.service;

import com.lumina_bank.analyticsservice.model.AnalyticsDailySummary;
import com.lumina_bank.analyticsservice.model.AnalyticsTransactionEvent;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class CsvGenerator {
    private static final String DAILYACTIVITY_HEADER =
            "date,totalIncome,totalExpense,transactionCount";

    private static final String TRANSACTIONHISTORY_HEADER =
            "date,paymentId,direction,category,amount,currency,status,riskScore,accountId";

    public byte[] generateDailyActivity(List<AnalyticsDailySummary> summaries) {
        StringBuilder sb = new StringBuilder();
        sb.append(DAILYACTIVITY_HEADER).append("\n");

        for (AnalyticsDailySummary s : summaries) {
            sb.append(s.getId().getDate()).append(",");
            sb.append(s.getTotalIncome()).append(",");
            sb.append(s.getTotalExpense()).append(",");
            sb.append(s.getTransactionCount()).append("\n");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] generateTransactionHistory(List<AnalyticsTransactionEvent> summaries) {
        StringBuilder sb = new StringBuilder();
        sb.append(TRANSACTIONHISTORY_HEADER).append("\n");

        for (AnalyticsTransactionEvent s : summaries) {
            sb.append(s.getProcessedAt()).append(",");
            sb.append(s.getPaymentId()).append(",");
            sb.append(s.getDirection()).append(",");
            sb.append(s.getCategory()).append(",");
            sb.append(s.getAmount()).append(",");
            sb.append(s.getCurrency()).append(",");
            sb.append(s.getStatus()).append(",");
            sb.append(s.getRiskScore()).append(",");
            sb.append(s.getAccountId()).append("\n");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
