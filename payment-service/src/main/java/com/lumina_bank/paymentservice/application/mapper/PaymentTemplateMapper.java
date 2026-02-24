package com.lumina_bank.paymentservice.application.mapper;

import com.lumina_bank.paymentservice.api.response.PaymentTemplateResponse;
import com.lumina_bank.paymentservice.domain.model.PaymentTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentTemplateMapper {
    public PaymentTemplateResponse toResponse(PaymentTemplate template){
        if(template==null) return null;

        return PaymentTemplateResponse.builder()
                .id(template.getId())
                .userId(template.getUserId())
                .type(template.getType())
                .name(template.getName())
                .description(template.getDescription())
                .fromCardNumber(template.getFromCardNumber())
                .toCardNumber(template.getToCardNumber())
                .amount(template.getAmount())
                .isRecurring(template.getIsRecurring())
                .nextExecutionTime(
                        template.getNextExecutionTime() != null
                                ? template.getNextExecutionTime().toString()
                                : null
                )
                .build();
    }

    public List<PaymentTemplateResponse> toResponseList(List<PaymentTemplate> templates){
        return templates.stream().map(this::toResponse).toList();
    }
}
