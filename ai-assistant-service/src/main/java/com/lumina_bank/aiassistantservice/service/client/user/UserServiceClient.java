package com.lumina_bank.aiassistantservice.service.client.user;

import com.lumina_bank.aiassistantservice.domain.dto.client.user.*;
import com.lumina_bank.aiassistantservice.security.FeignSecurityConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@FeignClient(name = "user-service", path = "/users",configuration = FeignSecurityConfig.class)
public interface UserServiceClient {

    @GetMapping("/my")
    ResponseEntity<UserResponse> getUser();

    @PutMapping("/me")
    ResponseEntity<UserResponse> updateUser(UserUpdateDto userDto);

    @GetMapping("/business-users/my")
    ResponseEntity<BusinessUserResponse> getBusinessUser();

    @PutMapping("/business-users/me")
    ResponseEntity<BusinessUserResponse> updateBusinessUser(BusinessUserUpdateDto bUserDto);

    @GetMapping("/business-users/providers")
    ResponseEntity<List<BusinessUserProviderResponse>> getProviders();
}