package com.lumina_bank.notificationservice.template;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailTemplateRenderer {

    public String renderVerification(String code) {
        return """
                Hello!
                
                Your verification code:
                
                %s
                
                This code is valid for 5 minutes.
                
                If you didn’t request this email — ignore it.
                """.formatted(code);
    }
}
