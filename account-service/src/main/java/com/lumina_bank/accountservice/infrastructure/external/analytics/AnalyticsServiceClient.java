package com.lumina_bank.accountservice.infrastructure.external.analytics;

import com.lumina_bank.accountservice.infrastructure.external.analytics.dto.LoanInfoExternalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "analytics-service", path = "/analytics")
public interface AnalyticsServiceClient {
    @GetMapping("/loan")
    LoanInfoExternalResponse getLoanInfo();
}
