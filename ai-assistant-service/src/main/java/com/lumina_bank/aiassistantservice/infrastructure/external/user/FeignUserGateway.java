package com.lumina_bank.aiassistantservice.infrastructure.external.user;

import com.lumina_bank.aiassistantservice.infrastructure.external.user.dto.*;
import com.lumina_bank.aiassistantservice.infrastructure.external.FeignExceptionMapper;
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
            return client.getUser();
        }catch (FeignException e){
            throw mapper.map(e);
        }
    }

    public UserResponse updateUser(UserUpdateRequest dto) {
        try {
            return client.updateUser(dto);
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }

    public BusinessUserResponse getBusinessUser(){
        try{
            return client.getBusinessUser();
        }catch (FeignException e){
            throw mapper.map(e);
        }
    }

    public BusinessUserResponse updateBusinessUser(BusinessUserUpdateRequest dto) {
        try {
            return client.updateBusinessUser(dto);
        } catch (FeignException e) {
            throw mapper.map(e);
        }
    }

    public List<BusinessUserProviderResponse> getProviders() {
        try{
            return Optional.ofNullable(client.getProviders()).orElse(List.of());
        }catch (FeignException e){
            throw mapper.map(e);
        }
    }
}
