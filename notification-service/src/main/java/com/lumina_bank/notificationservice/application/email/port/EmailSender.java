package com.lumina_bank.notificationservice.application.email.port;

import com.lumina_bank.notificationservice.domain.email.EmailMessage;

public interface EmailSender {
    void send(EmailMessage message);
}
