package com.lumina_bank.aiassistantservice.domain.assistant;

import com.lumina_bank.aiassistantservice.domain.enums.ParamType;

import java.util.List;

public record RequiredParam(
        String name,
        ParamType type,
        List<String> options,
        String description
) {}
