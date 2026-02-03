package com.lumina_bank.analyticsservice.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/users")
public interface UserServiceClient {

    @GetMapping("/name/{id}")
    ResponseEntity<String> getUserNameById(@PathVariable Long id);

    @GetMapping("/business-users/name/{id}")
    ResponseEntity<String> getBusinessUserNameById(@PathVariable Long id);
}
