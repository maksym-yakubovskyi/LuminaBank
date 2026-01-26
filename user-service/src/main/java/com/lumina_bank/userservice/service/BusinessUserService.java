package com.lumina_bank.userservice.service;

import com.lumina_bank.userservice.dto.BusinessUserProviderResponse;
import com.lumina_bank.userservice.enums.BusinessCategory;
import com.lumina_bank.userservice.model.BusinessUser;
import com.lumina_bank.userservice.repository.BusinessUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessUserService {
    private final BusinessUserRepository businessUserRepository;

    public List<BusinessUserProviderResponse> getProviders(BusinessCategory category){
        List<BusinessUser> providers = (category == null)
                ? businessUserRepository.findAllByActiveTrue()
                : businessUserRepository.findAllByActiveTrueAndCategory(category);

        return providers.stream()
                .map(BusinessUserProviderResponse::fromEntity)
                .toList();
    }

}

