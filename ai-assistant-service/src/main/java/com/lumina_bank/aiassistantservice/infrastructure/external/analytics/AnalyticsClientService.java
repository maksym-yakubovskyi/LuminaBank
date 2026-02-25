package com.lumina_bank.aiassistantservice.infrastructure.external.analytics;

import com.lumina_bank.aiassistantservice.infrastructure.external.analytics.dto.*;
import com.lumina_bank.aiassistantservice.infrastructure.security.FeignSecurityConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="analytics-service",path = "/analytics", configuration = FeignSecurityConfig.class)
public interface AnalyticsClientService {

    @GetMapping("/overview")
    AnalyticsMonthlyOverviewResponse getMonthlyOverview(@RequestParam Long accountId, @RequestParam(required = false) String  yearMonth);

    @GetMapping("/daily")
    AnalyticsDailyOverviewResponse getDailyOverview(@RequestParam Long accountId, @RequestParam(required = false) String  date);

    @GetMapping("/categories")
    List<AnalyticsCategoryResponse> getCategoriesAnalytics(@RequestParam(required = false) String yearMonth);

    @GetMapping("/top-recipients")
    List<AnalyticsTopRecipientResponse> getTopRecipients();

    @GetMapping("/ai-recommendations")
    RecommendationResponse getRecommendations();

    @GetMapping("/ai-forecast")
    AiForecastResponse getAiForecast();

}
