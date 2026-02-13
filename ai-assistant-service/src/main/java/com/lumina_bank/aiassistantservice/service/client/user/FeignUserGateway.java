package com.lumina_bank.aiassistantservice.service.client.user;

import com.lumina_bank.aiassistantservice.domain.dto.client.user.BusinessUserProviderResponse;
import com.lumina_bank.aiassistantservice.domain.dto.client.user.UserResponse;
import com.lumina_bank.aiassistantservice.domain.dto.client.user.UserUpdateDto;
import com.lumina_bank.aiassistantservice.domain.exception.ExternalServiceException;
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

    public UserResponse getUser(){
        try{
            var response = client.getUser();

            if(!response.getStatusCode().is2xxSuccessful()){
                throw new ExternalServiceException("User service returned status code " + response.getStatusCode());
            }

            return response.getBody();
        }catch (FeignException e){
            throw new ExternalServiceException("Сервіс користувачів тимчасово недоступний");
        }
    }

    public UserResponse updateUser(UserUpdateDto dto) {
        try {
            var response = client.updateUser(dto);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ExternalServiceException("User update failed");
            }

            return response.getBody();

        } catch (FeignException e) {
            throw new ExternalServiceException("User service return message " + e.getMessage());
        }
    }

    public List<BusinessUserProviderResponse> getProviders() {
        try{
            var response = client.getProviders();

            if(!response.getStatusCode().is2xxSuccessful()){
                throw new ExternalServiceException("User service returned status code " + response.getStatusCode());
            }
            return Optional.ofNullable(response.getBody()).orElse(List.of());
        }catch (FeignException e){
            throw new ExternalServiceException("Сервіс користувачів тимчасово недоступний");
        }
    }
}
