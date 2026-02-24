package com.lumina_bank.aiassistantservice.infrastructure.external.user;

import com.lumina_bank.aiassistantservice.infrastructure.external.user.dto.*;
import com.lumina_bank.aiassistantservice.infrastructure.security.FeignSecurityConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@FeignClient(name = "user-service", path = "/users",configuration = FeignSecurityConfig.class)
public interface UserServiceClient {

    @GetMapping("/my")
    UserResponse getUser();

    @PutMapping("/me")
    UserResponse updateUser(UserUpdateRequest userDto);

    @GetMapping("/business-users/my")
    BusinessUserResponse getBusinessUser();

    @PutMapping("/business-users/me")
    BusinessUserResponse updateBusinessUser(BusinessUserUpdateRequest bUserDto);

    @GetMapping("/business-users/providers")
    List<BusinessUserProviderResponse> getProviders();
}