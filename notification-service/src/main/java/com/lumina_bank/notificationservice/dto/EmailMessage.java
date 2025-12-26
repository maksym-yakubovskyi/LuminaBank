package com.lumina_bank.notificationservice.dto;

import com.lumina_bank.notificationservice.enums.EmailType;
import lombok.Builder;

@Builder
public record EmailMessage(
        String to,
        String subject,
        String body,
        EmailType emailType
) {
}