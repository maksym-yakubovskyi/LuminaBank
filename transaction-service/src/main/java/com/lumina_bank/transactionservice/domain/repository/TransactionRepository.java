package com.lumina_bank.transactionservice.domain.repository;

import com.lumina_bank.transactionservice.domain.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}