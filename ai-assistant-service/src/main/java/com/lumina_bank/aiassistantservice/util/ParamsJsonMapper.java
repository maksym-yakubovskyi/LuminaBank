package com.lumina_bank.aiassistantservice.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumina_bank.aiassistantservice.domain.model.Conversation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ParamsJsonMapper {

    private final ObjectMapper mapper;
    private final JavaType type;

    public ParamsJsonMapper(ObjectMapper mapper) {
        this.mapper = mapper;
        this.type = mapper.getTypeFactory()
                .constructMapType(Map.class, String.class, Object.class);
    }

    public Map<String, Object> merge(Conversation c, Map<String, Object> newParams) {

        Map<String, Object> params = read(c);
        params.putAll(newParams);
        write(c, params);
        return params;
    }

    public Map<String, Object> read(Conversation c) {
        try {
            return c.getCollectedParamsJson() == null
                    ? new HashMap<>()
                    : mapper.readValue(c.getCollectedParamsJson(), type);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private void write(Conversation c, Map<String, Object> params) {
        try {
            c.setCollectedParamsJson(mapper.writeValueAsString(params));
        } catch (Exception ignored) {}
    }
}
