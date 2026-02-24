package com.lumina_bank.userservice.application.mapper;

import com.lumina_bank.userservice.api.response.BusinessUserProviderResponse;
import com.lumina_bank.userservice.api.response.BusinessUserResponse;
import com.lumina_bank.userservice.domain.model.BusinessUser;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BusinessUserMapper {

    public BusinessUserResponse toResponse(BusinessUser user) {
        return BusinessUserResponse.builder()
                .id(user.getId())
                .companyName(user.getCompanyName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .adrpou(user.getAdrpou())
                .description(user.getDescription())
                .category(user.getCategory())
                .address(user.getAddress())
                .role(user.getRole().name())
                .build();
    }

    public BusinessUserProviderResponse toProviderResponse(BusinessUser user) {
        return BusinessUserProviderResponse.builder()
                .id(user.getId())
                .companyName(user.getCompanyName())
                .category(user.getCategory())
                .build();
    }

    public List<BusinessUserProviderResponse> toProviderResponseList(List<BusinessUser> users) {
        return users.stream()
                .map(this::toProviderResponse)
                .toList();
    }
}
