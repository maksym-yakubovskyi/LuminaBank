package com.lumina_bank.accountservice.infrastructure.external.user;

import com.lumina_bank.accountservice.infrastructure.external.user.dto.UserCheckExternalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/users")
public interface UserServiceClient {

    @GetMapping("/{id}/check")
    UserCheckExternalResponse checkUser(@PathVariable Long id);
}