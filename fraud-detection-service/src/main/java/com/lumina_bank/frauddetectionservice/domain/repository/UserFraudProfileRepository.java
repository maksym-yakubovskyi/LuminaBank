package com.lumina_bank.frauddetectionservice.domain.repository;

import com.lumina_bank.frauddetectionservice.domain.model.UserFraudProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFraudProfileRepository extends JpaRepository<UserFraudProfile,Long> {

}
