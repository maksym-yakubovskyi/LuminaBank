package com.lumina_bank.userservice.service;

import com.lumina_bank.common.enums.user.UserType;
import com.lumina_bank.userservice.dto.UserCheckResponse;
import com.lumina_bank.userservice.repository.BusinessUserRepository;
import com.lumina_bank.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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
}
