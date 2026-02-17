package com.lumina_bank.aiassistantservice.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumina_bank.aiassistantservice.domain.dto.RequiredParam;
import com.lumina_bank.aiassistantservice.domain.model.Conversation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class ParamsJsonMapper {

    private final ObjectMapper mapper;
    private final JavaType type = new ObjectMapper()
            .getTypeFactory()
            .constructMapType(Map.class, String.class, Object.class);

    public Map<String, Object> merge(Conversation c, Map<String, Object> newParams) {
        Map<String, Object> params = read(c);
        if (newParams != null) {
            params.putAll(newParams);
        }
        write(c, params);
        return params;
    }

    public Map<String, Object> read(Conversation c) {
        try {
            if (c.getCollectedParamsJson() == null) {
                return new HashMap<>();
            }

            return mapper.readValue(
                    c.getCollectedParamsJson(),
                    type
            );

        } catch (Exception e) {
            log.warn("Failed to read params JSON: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    public String toJsonSchema(List<RequiredParam> schema) {
        try {
            Map<String, Object> jsonSchema = new LinkedHashMap<>();

            for (RequiredParam param : schema) {

                Map<String, Object> field = new LinkedHashMap<>();

                field.put("type", param.type().name());

                if (param.description() != null && !param.description().isBlank()) {
                    field.put("description", param.description());
                }

                if (param.options() != null && !param.options().isEmpty()) {
                    field.put("options", param.options());
                }

                jsonSchema.put(param.name(), field);
            }

            return mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(jsonSchema);

        } catch (Exception e) {
            return "{}";
        }
    }

    private void write(Conversation c, Map<String, Object> params) {
        try {
            c.setCollectedParamsJson(
                    mapper.writeValueAsString(params)
            );
        } catch (Exception e) {
            log.warn("Failed to write params JSON", e);
        }
    }
}
