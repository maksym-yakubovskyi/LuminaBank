package com.lumina_bank.userservice.service;

import com.lumina_bank.common.dto.event.user_events.BusinessUserRegisteredEvent;
import com.lumina_bank.common.enums.user.Role;
import com.lumina_bank.userservice.dto.BusinessUserProviderResponse;
import com.lumina_bank.userservice.dto.BusinessUserUpdateDto;
import com.lumina_bank.userservice.enums.BusinessCategory;
import com.lumina_bank.userservice.exception.UserAlreadyExistsException;
import com.lumina_bank.userservice.exception.UserNotFoundException;
import com.lumina_bank.userservice.model.Address;
import com.lumina_bank.userservice.model.BusinessUser;
import com.lumina_bank.userservice.repository.BusinessUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    public BusinessUser updateBusinessUser(Long id, BusinessUserUpdateDto bUserDto) {
        BusinessUser bUser = getBusinessUserById(id);

        if(!bUserDto.email().equals(bUser.getEmail()) && businessUserRepository.existsByEmailAndActiveTrue(bUserDto.email())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        bUser.setEmail(bUserDto.email());
        bUser.setPhoneNumber(bUserDto.phoneNumber());
        bUser.setCompanyName(bUserDto.companyName());
        bUser.setAdrpou(bUserDto.adrpou());
        bUser.setCategory(bUserDto.category());
        bUser.setDescription(bUserDto.description());

        Address address = bUser.getAddress();

        if(address == null) {
            address = new Address();
        }

        address.setStreet(bUserDto.street());
        address.setCity(bUserDto.city());
        address.setCountry(bUserDto.country());
        address.setHouseNumber(bUserDto.houseNumber());
        address.setZipCode(bUserDto.zipCode());
        bUser.setAddress(address);

        return businessUserRepository.save(bUser);
    }

    @Transactional
    public void deleteBusinessUser(Long id){
        BusinessUser bUser = getBusinessUserById(id);
        bUser.setActive(false);
        businessUserRepository.save(bUser);
    }

    @Transactional(readOnly = true)
    public String getBusinessUserNameById(Long id) {
        BusinessUser businessUser = businessUserRepository.findById(id)
                .orElseThrow(() ->new UsernameNotFoundException("Business User not found with id=" + id));

        return businessUser.getCompanyName();
    }
}