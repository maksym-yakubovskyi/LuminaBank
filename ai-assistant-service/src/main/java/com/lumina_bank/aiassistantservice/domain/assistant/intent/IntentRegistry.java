package com.lumina_bank.aiassistantservice.domain.assistant.intent;

import com.lumina_bank.aiassistantservice.domain.enums.Intent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class IntentRegistry {

    private final Map<Intent, IntentDefinition> map;

    @Autowired
    public IntentRegistry(List<IntentDefinition> defs) {
        this.map = defs.stream()
                .collect(Collectors.toUnmodifiableMap(IntentDefinition::intent, d -> d));
    }
    public IntentDefinition get(Intent intent) {
        return map.get(intent);
    }
}
