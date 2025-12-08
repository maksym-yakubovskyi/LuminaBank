package com.lumina_bank.userservice.service;

import com.lumina_bank.userservice.dto.UserCreateDto;
import com.lumina_bank.userservice.dto.UserUpdateDto;
import com.lumina_bank.userservice.enums.Role;
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

    @Transactional
    public User createUser(UserCreateDto userDto) {
        log.debug("Attempting to create user with email={}", userDto.email());

        if (userRepository.existsByEmailAndActiveTrue(userDto.email())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        Address address = Address.builder().
                street(userDto.street()).
                city(userDto.city()).
                country(userDto.country()).
                houseNumber(userDto.houseNumber()).
                zipCode(userDto.zipCode()).
                build();

        User user = User.builder().
                email(userDto.email()).
                password(userDto.password()).
                firstName(userDto.firstName()).
                lastName(userDto.lastName()).
                phoneNumber(userDto.phoneNumber()).
                birthDate(userDto.birthDate()).
                address(address).
                role(Role.USER).
                active(Boolean.TRUE).
                build();

        return userRepository.save(user);
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
}
