package com.lumina_bank.userservice.domain.repository;

import com.lumina_bank.userservice.domain.enums.BusinessCategory;
import com.lumina_bank.userservice.domain.model.BusinessUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessUserRepository extends JpaRepository<BusinessUser,Long> {
    boolean existsByIdAndActiveTrue(Long authUserId);
    List<BusinessUser> findAllByActiveTrue();

    List<BusinessUser> findAllByActiveTrueAndCategory(BusinessCategory category);

    boolean existsByEmailAndActiveTrue(String email);

    Optional<BusinessUser> findByIdAndActiveTrue(Long id);
}
