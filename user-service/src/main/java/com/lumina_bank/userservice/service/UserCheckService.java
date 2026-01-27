package com.lumina_bank.userservice.service;

import com.lumina_bank.common.enums.user.UserType;
import com.lumina_bank.userservice.dto.UserCheckResponse;
import com.lumina_bank.userservice.repository.BusinessUserRepository;
import com.lumina_bank.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCheckService {
    private final UserRepository userRepository;
    private final BusinessUserRepository businessUserRepository;

    public UserCheckResponse checkUser(Long userId) {
        return userRepository.findById(userId)
                .map(u-> new UserCheckResponse(u.getId(),true,u.getActive(), UserType.INDIVIDUAL_USER))
                .orElseGet(()-> businessUserRepository.findById(userId)
                        .map(bu-> new UserCheckResponse(bu.getId(),true,bu.getActive(),UserType.BUSINESS_USER))
                        .orElse(new UserCheckResponse(userId,false,false,null)));
    }

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