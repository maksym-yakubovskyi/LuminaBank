package com.lumina_bank.notificationservice.application.service;

import com.lumina_bank.notificationservice.domain.model.UserContactInfo;
import com.lumina_bank.notificationservice.domain.repository.UserContactInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserContactInfoService {

    private final UserContactInfoRepository userContactRepository;

    public String getEmail(Long userId){
        return userContactRepository
                .findById(userId)
                .map(UserContactInfo::getEmail)
                .orElse(null);
    }
}
