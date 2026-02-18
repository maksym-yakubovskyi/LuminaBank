package com.lumina_bank.aiassistantservice.service.client.user;

import com.lumina_bank.aiassistantservice.domain.dto.client.user.*;
import com.lumina_bank.aiassistantservice.util.FeignExceptionMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeignUserGateway {
    private final UserServiceClient client;
    private final FeignExceptionMapper mapper;

    public UserResponse getUser(){
        try{
            return client.getUser().getBody();
        }catch (FeignException e){
            throw mapper.map(e);
        }
    }

    public UserResponse updateUser(UserUpdateDto dto) {
        try {
            return client.updateUser(dto).getBody();
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }

    public BusinessUserResponse getBusinessUser(){
        try{
            return client.getBusinessUser().getBody();
        }catch (FeignException e){
            throw mapper.map(e);
        }
    }

    public BusinessUserResponse updateBusinessUser(BusinessUserUpdateDto dto) {
        try {
            return client.updateBusinessUser(dto).getBody();
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }

    public List<BusinessUserProviderResponse> getProviders() {
        try{
            return Optional.ofNullable(client.getProviders().getBody()).orElse(List.of());
        }catch (FeignException e){
            throw mapper.map(e);
        }
    }
}
