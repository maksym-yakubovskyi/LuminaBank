package com.lumina_bank.aiassistantservice.infrastructure.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumina_bank.aiassistantservice.domain.exception.ServiceCallException;
import com.lumina_bank.common.exception.ErrorResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class FeignExceptionMapper {

    private final ObjectMapper mapper;

    public ServiceCallException map(FeignException e) {
        log.warn("Feign call failed. Status: {}, Message: {}",
                e.status(), e.getMessage());
        try {
            String body = e.contentUTF8();

            if (body != null && !body.isBlank()) {
                ErrorResponse error =
                        mapper.readValue(body, ErrorResponse.class);

                return new ServiceCallException(error.message());
            }

        } catch (Exception ex) {
            log.warn("Cannot parse feign error body: {}", ex.getMessage());
        }

        return new ServiceCallException("Сервіс тимчасово недоступний");
    }
}

