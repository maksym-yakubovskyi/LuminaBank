package com.lumina_bank.accountservice.domain.repository;

import com.lumina_bank.accountservice.domain.enums.LoanStatus;
import com.lumina_bank.accountservice.domain.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan,Long> {
    boolean existsByCreditAccountIdAndStatusIn(Long accountId, List<LoanStatus> active);

    Optional<Loan> findByCreditAccountIdAndStatusIn(Long id, List<LoanStatus> active);

    List<Loan> findAllByCreditAccount_UserIdAndStatusIn(
            Long userId,
            List<LoanStatus> statuses
    );
}
