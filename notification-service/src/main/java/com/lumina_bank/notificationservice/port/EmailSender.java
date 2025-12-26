package com.lumina_bank.notificationservice.port;

import com.lumina_bank.notificationservice.dto.EmailMessage;

public interface EmailSender {
    void send(EmailMessage message);
}
