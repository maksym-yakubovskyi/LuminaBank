package com.lumina_bank.analyticsservice.application.analytics;

import com.lumina_bank.analyticsservice.api.response.analytics.LoanInfoResponse;
import com.lumina_bank.analyticsservice.application.mapper.LoanInfoMapper;
import com.lumina_bank.analyticsservice.domain.analysis.FinancialAggregate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoanAnalyticsFacade {

    private final FinancialAggregationService aggregationService;
    private final LoanInfoMapper mapper;

    @Transactional(readOnly = true)
    public LoanInfoResponse buildLoanInfo(Long userId) {
        FinancialAggregate agg =
                aggregationService.aggregateLastMonths(userId, 5);

        return mapper.toResponse(agg);
    }
}
