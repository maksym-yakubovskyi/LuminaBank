package com.lumina_bank.analyticsservice.infrastructure.external.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/users")
public interface UserServiceClient {

    @GetMapping("/name/{id}")
    String getUserNameById(@PathVariable Long id);

    @GetMapping("/business-users/name/{id}")
    String getBusinessUserNameById(@PathVariable Long id);
}
