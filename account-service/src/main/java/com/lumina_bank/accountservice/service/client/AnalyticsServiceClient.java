package com.lumina_bank.accountservice.service.client;

import com.lumina_bank.accountservice.dto.client.LoanInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "analytics-service", path = "/analytics")
public interface AnalyticsServiceClient {
    @GetMapping("/loan")
    ResponseEntity<LoanInfoResponse> getLoanInfo();
}
