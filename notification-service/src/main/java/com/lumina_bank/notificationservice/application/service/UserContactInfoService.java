package com.lumina_bank.notificationservice.application.service;

import com.lumina_bank.notificationservice.domain.model.UserContactInfo;
import com.lumina_bank.notificationservice.domain.repository.UserContactInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserContactInfoService {

    private final UserContactInfoRepository userContactRepository;

    @Transactional(readOnly = true)
    public String getEmail(Long userId){
        return userContactRepository
                .findById(userId)
                .map(UserContactInfo::getEmail)
                .orElse(null);
    }

    @Transactional
    public void saveOrUpdate(Long userId, String email) {

        if (userId == null || email == null || email.isBlank()) {
            log.warn("Skipping contact info save: invalid data userId={}, email={}", userId, email);
            return;
        }

        UserContactInfo contact = userContactRepository
                .findById(userId)
                .map(existing -> {
                    existing.setEmail(email);
                    existing.setUpdatedAt(LocalDateTime.now());
                    return existing;
                })
                .orElseGet(() -> UserContactInfo.builder()
                        .userId(userId)
                        .email(email)
                        .updatedAt(LocalDateTime.now())
                        .build()
                );

        userContactRepository.save(contact);

        log.info("Saved contact info userId={}, email={}", userId, email);
    }

}
