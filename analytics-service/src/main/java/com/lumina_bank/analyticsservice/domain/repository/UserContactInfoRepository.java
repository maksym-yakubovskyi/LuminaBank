package com.lumina_bank.analyticsservice.domain.repository;

import com.lumina_bank.analyticsservice.domain.model.UserContactInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserContactInfoRepository extends JpaRepository<UserContactInfo,Long> {
}
