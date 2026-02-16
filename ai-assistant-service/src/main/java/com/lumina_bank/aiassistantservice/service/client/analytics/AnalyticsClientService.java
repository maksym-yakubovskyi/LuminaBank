package com.lumina_bank.aiassistantservice.service.client.analytics;

import com.lumina_bank.aiassistantservice.domain.dto.client.analytics.*;
import com.lumina_bank.aiassistantservice.security.FeignSecurityConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@FeignClient(name="analytics-service",path = "/analytics", configuration = FeignSecurityConfig.class)
public interface AnalyticsClientService {

    @GetMapping("/overview")
    ResponseEntity<AnalyticsMonthlyOverviewResponse> getMonthlyOverview(@RequestParam Long accountId,@RequestParam(required = false) String  yearMonth);

    @GetMapping("/daily")
    ResponseEntity<AnalyticsDailyOverviewResponse> getDailyOverview(@RequestParam Long accountId, @RequestParam(required = false) String  date);

    @GetMapping("/categories")
    ResponseEntity<List<AnalyticsCategoryResponse>> getCategoriesAnalytics(@RequestParam(required = false) String yearMonth);

    @GetMapping("/top-recipients")
    ResponseEntity<List<AnalyticsTopRecipientResponse>> getTopRecipients();

    @GetMapping("/ai-recommendations")
    ResponseEntity<RecommendationResponse> getRecommendations();

    @GetMapping("/ai-forecast")
    ResponseEntity<AiForecastResponse> getAiForecast();

}
