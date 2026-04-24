package com.lumina_bank.accountservice.infrastructure.external.analytics;

import com.lumina_bank.accountservice.infrastructure.external.analytics.dto.LoanInfoExternalResponse;
import com.lumina_bank.accountservice.infrastructure.security.FeignSecurityConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "analytics-service", path = "/analytics",configuration = FeignSecurityConfig.class)
public interface AnalyticsServiceClient {
    @GetMapping("/loan")
    LoanInfoExternalResponse getLoanInfo();
}
