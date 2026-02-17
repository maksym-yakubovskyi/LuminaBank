package com.lumina_bank.aiassistantservice.domain.dto.client.analytics;

import java.math.BigDecimal;

public record AnalyticsCategoryResponse(
        String category,
        BigDecimal totalAmount,
        Integer percentage
) {}
