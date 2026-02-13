package com.lumina_bank.aiassistantservice.domain.result.data.payment;

import com.lumina_bank.aiassistantservice.domain.dto.client.payment.PaymentTemplateResponse;
import com.lumina_bank.aiassistantservice.domain.result.AssistantData;

import java.util.List;

public record PaymentTemplatesData(
        List<PaymentTemplateResponse>  paymentTemplates
) implements AssistantData {}
