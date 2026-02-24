package com.lumina_bank.accountservice.domain.repository;

import com.lumina_bank.accountservice.domain.enums.InstallmentStatus;
import com.lumina_bank.accountservice.domain.model.LoanInstallment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment,Long> {
    List<LoanInstallment> findAllByStatusAndDueDate(InstallmentStatus installmentStatus, LocalDate today);

    List<LoanInstallment> findAllByStatusAndDueDateBefore(InstallmentStatus installmentStatus, LocalDate today);

    List<LoanInstallment> findAllByStatus(InstallmentStatus installmentStatus);
}
