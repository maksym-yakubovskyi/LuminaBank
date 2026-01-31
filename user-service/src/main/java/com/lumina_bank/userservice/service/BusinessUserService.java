package com.lumina_bank.userservice.service;

import com.lumina_bank.common.dto.event.user_events.BusinessUserRegisteredEvent;
import com.lumina_bank.userservice.dto.BusinessUserProviderResponse;
import com.lumina_bank.common.enums.user.BusinessCategory;
import com.lumina_bank.userservice.model.BusinessUser;
import com.lumina_bank.userservice.repository.BusinessUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessUserService {
    private final BusinessUserRepository businessUserRepository;
    private final UserCheckService userCheckService;

    public List<BusinessUserProviderResponse> getProviders(BusinessCategory category){
        List<BusinessUser> providers = (category == null)
                ? businessUserRepository.findAllByActiveTrue()
                : businessUserRepository.findAllByActiveTrueAndCategory(category);

        return providers.stream()
                .map(BusinessUserProviderResponse::fromEntity)
                .toList();
    }

    @Transactional
    public void createUser(BusinessUserRegisteredEvent event) {
        log.debug("Attempting to create b user with id={}", event.authUserId());

        if (userCheckService.checkUserExistsByEmailAndId(event.authUserId(),event.email())) {
            log.warn("B User already exists");
            return;
        }

        BusinessUser businessUser = BusinessUser.builder()
                .id(event.authUserId())
                .email(event.email())
                .phoneNumber(event.phoneNumber())
                .companyName(event.companyName())
                .adrpou(event.adrpou())
                .category(BusinessCategory.valueOf(event.category()))
                .createdAt(event.registeredAt())
                .active(Boolean.TRUE)
                .build();

        businessUserRepository.save(businessUser);

        log.debug("Created B user with id={}", event.authUserId());
    }
}