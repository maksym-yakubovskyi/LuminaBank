package com.lumina_bank.frauddetectionservice.repository;

import com.lumina_bank.frauddetectionservice.model.UserFraudProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFraudProfileRepository extends JpaRepository<UserFraudProfile,Long> {

}
