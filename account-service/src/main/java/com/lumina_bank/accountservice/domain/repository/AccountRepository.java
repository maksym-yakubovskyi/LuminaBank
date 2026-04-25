package com.lumina_bank.accountservice.domain.repository;

import com.lumina_bank.accountservice.domain.enums.AccountType;
import com.lumina_bank.accountservice.domain.enums.Status;
import com.lumina_bank.accountservice.domain.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByIban(String iban);

    Optional<Account> findByUserIdAndType(Long userId, AccountType accountType);

    List<Account> findAllByUserIdAndStatusOrderByCreatedAtAsc(Long userId, Status status);

    Optional<Account> findByIdAndUserIdAndStatus(Long id, Long userId, Status status);

    List<Account> findAllByUserIdAndStatusAndType(Long userId, Status status, AccountType type);

    List<Account> findAllByUserIdOrderByCreatedAtAsc(Long userId);
}