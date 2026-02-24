package com.lumina_bank.aiassistantservice.infrastructure.external.analytics.dto;

import java.math.BigDecimal;

public record AnalyticsCategoryResponse(
        String category,
        BigDecimal totalAmount,
        Integer percentage
) {}
