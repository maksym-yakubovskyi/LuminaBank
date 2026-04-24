package com.lumina_bank.analyticsservice.application.service;


import com.lumina_bank.analyticsservice.domain.model.UserContactInfo;
import com.lumina_bank.analyticsservice.domain.repository.UserContactInfoRepository;
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
    public String getName(Long userId){
        return userContactRepository
                .findById(userId)
                .map(UserContactInfo::getName)
                .orElse(
                        "Recipient #" + userId
                );
    }

    @Transactional
    public void saveOrUpdate(Long userId, String name) {

        if (userId == null || name == null || name.isBlank()) {
            log.warn("Skipping contact info save: invalid data userId={}, name={}", userId, name);
            return;
        }

        UserContactInfo contact = userContactRepository
                .findById(userId)
                .map(existing -> {
                    existing.setName(name);
                    existing.setUpdatedAt(LocalDateTime.now());
                    return existing;
                })
                .orElseGet(() -> UserContactInfo.builder()
                        .userId(userId)
                        .name(name)
                        .updatedAt(LocalDateTime.now())
                        .build()
                );

        userContactRepository.save(contact);

        log.info("Saved contact info userId={}, name={}", userId, name);
    }

}
