package com.lumina_bank.analyticsservice.api.controller;

import com.lumina_bank.analyticsservice.api.response.analytics.*;
import com.lumina_bank.analyticsservice.application.analytics.AnalyticsQueryService;
import com.lumina_bank.analyticsservice.application.analytics.ForecastService;
import com.lumina_bank.analyticsservice.application.analytics.LoanAnalyticsFacade;
import com.lumina_bank.analyticsservice.application.analytics.RecommendationService;
import com.lumina_bank.common.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@Slf4j
public class AnalyticsController {
    private final AnalyticsQueryService analyticsQueryService;
    private final ForecastService forecastService;
    private final LoanAnalyticsFacade loanAnalyticsFacade;
    private final RecommendationService  recommendationService;

    @GetMapping("/overview")
    public ResponseEntity<?> getOverview(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam Long accountId,
            @RequestParam(required = false) String month // yyyy-MM
    ){
        Long userId = JwtUtils.extractUserId(jwt);

        YearMonth yearMonth = (month == null)
                ? YearMonth.now()
                : YearMonth.parse(month);

        AnalyticsMonthlyOverviewResponse response =
                analyticsQueryService.getMonthlyOverview(userId, accountId, yearMonth);

        log.info(
                "GET /analytics/overview - Overview fetched successfully for userId={}, accountId={}",
                userId, accountId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/daily")
    public ResponseEntity<?> getDailyAnalytics(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam Long accountId,
            @RequestParam(required = false) String date){

        Long userId = JwtUtils.extractUserId(jwt);

        LocalDate localDate = (date == null)
                ? LocalDate.now()
                : LocalDate.parse(date);

        AnalyticsDailyOverviewResponse response =
                analyticsQueryService.getDailyOverview(userId,accountId,localDate);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getCategoriesAnalytics(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) String month
    ){
        Long userId = JwtUtils.extractUserId(jwt);

        YearMonth yearMonth = (month == null)
                ? YearMonth.now()
                : YearMonth.parse(month);

        List<AnalyticsCategoryResponse> response =
                analyticsQueryService.getCategoryExpenses(userId, yearMonth);

        log.info(
                "GET /analytics/categories - Categories analytics fetched successfully for userId={}, categoriesCount={}",
                userId, response.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-recipients")
    public ResponseEntity<?> getTopRecipients(
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = JwtUtils.extractUserId(jwt);

        List<AnalyticsTopRecipientResponse> response =
                analyticsQueryService.getTopRecipients(userId);

        log.info(
                "GET /analytics/top-recipients - Top recipients fetched successfully for userId={}, count={}",
                userId, response.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/ai-forecast")
    public ResponseEntity<?> getAiForecast(
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = JwtUtils.extractUserId(jwt);

        AiForecastResponse response =
                forecastService.buildForecast(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/ai-recommendations")
    public ResponseEntity<?> getRecommendations(@AuthenticationPrincipal Jwt jwt){
        Long userId = JwtUtils.extractUserId(jwt);

        RecommendationResponse response = recommendationService.buildRecommendationInfo(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/loan")
    public ResponseEntity<?> getLoanInfo(@AuthenticationPrincipal Jwt jwt){
        Long userId = JwtUtils.extractUserId(jwt);

        LoanInfoResponse response = loanAnalyticsFacade.buildLoanInfo(userId);

        return ResponseEntity.ok(response);
    }
}