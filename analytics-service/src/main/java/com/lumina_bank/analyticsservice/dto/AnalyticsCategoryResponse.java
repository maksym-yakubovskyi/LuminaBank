package com.lumina_bank.analyticsservice.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AnalyticsCategoryResponse(
        String category,
        BigDecimal totalAmount,
        Integer percentage
) {}
