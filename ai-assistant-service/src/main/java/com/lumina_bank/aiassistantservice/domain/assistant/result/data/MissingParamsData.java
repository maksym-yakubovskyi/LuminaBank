package com.lumina_bank.aiassistantservice.domain.assistant.result.data;

import com.lumina_bank.aiassistantservice.domain.assistant.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import com.lumina_bank.aiassistantservice.domain.assistant.result.AssistantData;

import java.util.List;

public record MissingParamsData(
        Intent intent,
        List<RequiredParam> missingParams,
        String currentParam
) implements AssistantData {}
