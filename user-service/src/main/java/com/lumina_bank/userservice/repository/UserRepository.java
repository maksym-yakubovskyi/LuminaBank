package com.lumina_bank.userservice.repository;

import com.lumina_bank.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByIdAndActiveTrue(Long id);

    boolean existsByEmailAndActiveTrue(String email);
}
