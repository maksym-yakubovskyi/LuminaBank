package com.lumina_bank.frauddetectionservice.repository;

import com.lumina_bank.frauddetectionservice.model.UserRecipientStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRecipientStatsRepository extends JpaRepository<UserRecipientStats,Long> {
    Optional<UserRecipientStats> findByUserIdAndToCardNumber(Long userId, String cardNumber);
}
