package com.lumina_bank.accountservice.repository;

import com.lumina_bank.accountservice.enums.AccountType;
import com.lumina_bank.accountservice.enums.Status;
import com.lumina_bank.accountservice.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByIban(String iban);

    Optional<Account> findByUserIdAndType(Long userId, AccountType accountType);

    List<Account> findAllByUserIdAndStatus(Long userId, Status status);
}