package com.lumina_bank.userservice.application.service;

import com.lumina_bank.userservice.domain.repository.BusinessUserRepository;
import com.lumina_bank.userservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCheckService {
    private final UserRepository userRepository;
    private final BusinessUserRepository businessUserRepository;

    public boolean checkUserExistsByEmailAndId(Long userId, String email) {
        if (userRepository.existsByEmailAndActiveTrue(email) ||
        businessUserRepository.existsByEmailAndActiveTrue(email)){
            log.warn("User with email {} already exists", email);
            return true;
        }

        if (userRepository.existsByIdAndActiveTrue(userId) ||
        businessUserRepository.existsByIdAndActiveTrue(userId)) {
            log.warn("User with id {} already exists", userId);
            return true;
        }

        return false;
    }
}