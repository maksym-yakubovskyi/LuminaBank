package com.lumina_bank.aiassistantservice.domain.result.data;

import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.result.AssistantData;

import java.util.List;

public record MissingParamsData(
        Intent intent,
        List<RequiredParam> missingParams
) implements AssistantData {}
