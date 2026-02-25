package com.lumina_bank.notificationservice.domain.email;

import com.lumina_bank.notificationservice.domain.enums.EmailType;
import lombok.Builder;

@Builder
public record EmailMessage(
        String to,
        String subject,
        String body,
        EmailType emailType
) {
}