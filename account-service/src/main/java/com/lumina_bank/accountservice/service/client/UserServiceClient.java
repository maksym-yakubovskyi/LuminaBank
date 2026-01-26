package com.lumina_bank.accountservice.service.client;

import com.lumina_bank.accountservice.dto.client.UserCheckResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/users")
public interface UserServiceClient {

    @GetMapping("/{id}/check")
    ResponseEntity<UserCheckResponse> checkUser(@PathVariable Long id);
}