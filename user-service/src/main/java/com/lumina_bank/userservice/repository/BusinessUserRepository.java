package com.lumina_bank.userservice.repository;

import com.lumina_bank.common.enums.user.BusinessCategory;
import com.lumina_bank.userservice.model.BusinessUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessUserRepository extends JpaRepository<BusinessUser,Long> {
    boolean existsByIdAndActiveTrue(Long authUserId);
    List<BusinessUser> findAllByActiveTrue();

    List<BusinessUser> findAllByActiveTrueAndCategory(BusinessCategory category);

    boolean existsByEmailAndActiveTrue(String email);
}
