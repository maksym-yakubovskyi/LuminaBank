package com.lumina_bank.userservice.application.service;

import com.lumina_bank.common.dto.event.user_events.BusinessUserRegisteredEvent;
import com.lumina_bank.common.enums.user.Role;
import com.lumina_bank.userservice.api.request.BusinessUserUpdateRequest;
import com.lumina_bank.userservice.domain.enums.BusinessCategory;
import com.lumina_bank.userservice.domain.exception.UserAlreadyExistsException;
import com.lumina_bank.userservice.domain.exception.UserNotFoundException;
import com.lumina_bank.userservice.domain.model.Address;
import com.lumina_bank.userservice.domain.model.BusinessUser;
import com.lumina_bank.userservice.domain.repository.BusinessUserRepository;
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

    @Transactional(readOnly = true)
    public List<BusinessUser> getProviders(BusinessCategory category){
        return (category == null)
                ? businessUserRepository.findAllByActiveTrue()
                : businessUserRepository.findAllByActiveTrueAndCategory(category);
    }

    @Transactional
    public void createUser(BusinessUserRegisteredEvent event) {
        log.debug("Attempting to create b user with id={}", event.authUserId());

        if (userCheckService.checkUserExistsByEmailAndId(event.authUserId(),event.email())) {
            throw new UserAlreadyExistsException("Business User already exists");
        }

        BusinessUser businessUser = BusinessUser.builder()
                .id(event.authUserId())
                .email(event.email())
                .phoneNumber(event.phoneNumber())
                .companyName(event.companyName())
                .adrpou(event.adrpou())
                .category(BusinessCategory.valueOf(event.category()))
                .role(Role.BUSINESS_USER)
                .createdAt(event.registeredAt())
                .active(Boolean.TRUE)
                .build();

        businessUserRepository.save(businessUser);

        log.debug("Created B user with id={}", event.authUserId());
    }

    @Transactional(readOnly = true)
    public BusinessUser getBusinessUserById(Long id) {
        return businessUserRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }

    @Transactional
    public BusinessUser updateBusinessUser(Long id, BusinessUserUpdateRequest request) {
        BusinessUser bUser = getBusinessUserById(id);

        if(!request.email().equals(bUser.getEmail())
                && businessUserRepository.existsByEmailAndActiveTrue(request.email())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        bUser.setEmail(request.email());
        bUser.setPhoneNumber(request.phoneNumber());
        bUser.setCompanyName(request.companyName());
        bUser.setAdrpou(request.adrpou());
        bUser.setCategory(request.category());
        bUser.setDescription(request.description());

        Address address = bUser.getAddress();

        if(address == null) {
            address = new Address();
        }

        address.setStreet(request.street());
        address.setCity(request.city());
        address.setCountry(request.country());
        address.setHouseNumber(request.houseNumber());
        address.setZipCode(request.zipCode());
        bUser.setAddress(address);

        return businessUserRepository.save(bUser);
    }

    @Transactional
    public void deleteBusinessUser(Long id){
        BusinessUser bUser = getBusinessUserById(id);
        bUser.setActive(false);
        businessUserRepository.save(bUser);
    }
}