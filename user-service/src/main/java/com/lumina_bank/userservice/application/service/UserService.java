package com.lumina_bank.userservice.application.service;

import com.lumina_bank.common.dto.event.user_events.IndividualUserRegisteredEvent;
import com.lumina_bank.common.enums.user.Role;
import com.lumina_bank.userservice.api.request.UserUpdateRequest;
import com.lumina_bank.userservice.domain.exception.UserAlreadyExistsException;
import com.lumina_bank.userservice.domain.exception.UserNotFoundException;
import com.lumina_bank.userservice.domain.model.Address;
import com.lumina_bank.userservice.domain.model.User;
import com.lumina_bank.userservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserCheckService userCheckService;

    @Transactional
    public void createUser(IndividualUserRegisteredEvent event) {
        log.debug("Attempting to create user with id={}", event.authUserId());

        if (userCheckService.checkUserExistsByEmailAndId(event.authUserId(),event.email())) {
            throw new UserAlreadyExistsException("User already exists");
        }

        User user = User.builder()
                .id(event.authUserId())
                .email(event.email())
                .firstName(event.firstName())
                .lastName(event.lastName())
                .phoneNumber(event.phoneNumber())
                .birthDate(event.birthDate())
                .role(Role.INDIVIDUAL_USER)
                .registeredAt(event.registeredAt())
                .active(Boolean.TRUE)
                .build();

        userRepository.save(user);

        log.debug("Created user with id={}", event.authUserId());
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        log.debug("Retrieving user with id={}", id);

        return userRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }

    @Transactional
    public User updateUser(Long id, UserUpdateRequest request) {
        log.debug("Updating user id={}", id);

        User user = getUserById(id);

        if (!request.email().equals(user.getEmail())
                && userRepository.existsByEmailAndActiveTrue(request.email())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhoneNumber(request.phoneNumber());
        user.setBirthDate(request.birthDate());
        user.setEmail(request.email());

        Address address = user.getAddress();

        if (address == null) {
            address = new Address();
        }

        address.setStreet(request.street());
        address.setCity(request.city());
        address.setCountry(request.country());
        address.setHouseNumber(request.houseNumber());
        address.setZipCode(request.zipCode());
        user.setAddress(address);

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.debug("Deleting user id={}", id);

        User user = getUserById(id);
        user.setActive(false);
        userRepository.save(user);
    }
}