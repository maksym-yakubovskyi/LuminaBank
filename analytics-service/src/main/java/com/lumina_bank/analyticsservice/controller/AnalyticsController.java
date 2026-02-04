package com.lumina_bank.analyticsservice.controller;

import com.lumina_bank.analyticsservice.dto.AnalyticsCategoryResponse;
import com.lumina_bank.analyticsservice.dto.AnalyticsMonthlyOverviewResponse;
import com.lumina_bank.analyticsservice.dto.AnalyticsTopRecipientResponse;
import com.lumina_bank.analyticsservice.service.AnalyticsService;
import com.lumina_bank.common.exception.JwtMissingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@Slf4j
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @GetMapping("/overview")
    public ResponseEntity<?> getOverview(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam Long accountId,
            @RequestParam(required = false) String month // yyyy-MM
    ){
        log.info("GET /analytics/overview - Fetching overview");

        if (jwt == null) throw new JwtMissingException("JWT token is required");

        Long userId = Long.valueOf(jwt.getSubject());

        YearMonth yearMonth = (month == null)
                ? YearMonth.now()
                : YearMonth.parse(month);

        AnalyticsMonthlyOverviewResponse response =
                analyticsService.getMonthlyOverview(userId, accountId, yearMonth);

        log.info(
                "GET /analytics/overview - Overview fetched successfully for userId={}, accountId={}",
                userId, accountId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getCategoriesAnalytics(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) String month
    ){
        log.info("GET /analytics/categories - Fetching categories analytics");
        if (jwt == null) throw new JwtMissingException("JWT token is required");

        Long userId = Long.valueOf(jwt.getSubject());

        YearMonth yearMonth = (month == null)
                ? YearMonth.now()
                : YearMonth.parse(month);

        List<AnalyticsCategoryResponse> response =
                analyticsService.getCategoryExpenses(userId, yearMonth);

        log.info(
                "GET /analytics/categories - Categories analytics fetched successfully for userId={}, categoriesCount={}",
                userId, response.size());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-recipients")
    public ResponseEntity<?> getTopRecipients(
            @AuthenticationPrincipal Jwt jwt
    ) {
        log.info("GET /analytics/top-recipients - Fetching top recipients");
        if (jwt == null) {
            throw new JwtMissingException("JWT token is required");
        }

        Long userId = Long.valueOf(jwt.getSubject());

        List<AnalyticsTopRecipientResponse> response =
                analyticsService.getTopRecipients(userId);

        log.info(
                "GET /analytics/top-recipients - Top recipients fetched successfully for userId={}, count={}",
                userId, response.size());

        return ResponseEntity.ok(response);
    }
}