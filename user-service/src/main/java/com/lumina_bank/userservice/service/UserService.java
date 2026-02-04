package com.lumina_bank.userservice.service;

import com.lumina_bank.common.dto.event.user_events.IndividualUserRegisteredEvent;
import com.lumina_bank.common.enums.user.Role;
import com.lumina_bank.userservice.dto.UserUpdateDto;
import com.lumina_bank.userservice.exception.UserAlreadyExistsException;
import com.lumina_bank.userservice.exception.UserNotFoundException;
import com.lumina_bank.userservice.model.Address;
import com.lumina_bank.userservice.model.User;
import com.lumina_bank.userservice.repository.UserRepository;
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
            log.warn("User already exists");
            return;
        }

        User user = User.builder()
                .id(event.authUserId())
                .email(event.email())
                .firstName(event.firstName())
                .lastName(event.lastName())
                .phoneNumber(event.phoneNumber())
                .birthDate(event.birthDate())
                .role(Role.USER)
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
    public User updateUser(Long id, UserUpdateDto userDto) {
        log.debug("Updating user id={}", id);

        User user = getUserById(id);

        if (!userDto.email().equals(user.getEmail()) && userRepository.existsByEmailAndActiveTrue(userDto.email())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());
        user.setPhoneNumber(userDto.phoneNumber());
        user.setBirthDate(userDto.birthDate());
        user.setEmail(userDto.email());

        Address address = user.getAddress();

        if (address == null) {
            address = new Address();
        }

        address.setStreet(userDto.street());
        address.setCity(userDto.city());
        address.setCountry(userDto.country());
        address.setHouseNumber(userDto.houseNumber());
        address.setZipCode(userDto.zipCode());
        user.setAddress(address);

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

    @Transactional(readOnly = true)
    public String  getUserNameById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));

        return user.getFirstName() + " " + user.getLastName();
    }
}