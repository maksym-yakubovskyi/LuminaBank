package com.lumina_bank.accountservice.repository;

import com.lumina_bank.accountservice.enums.Status;
import com.lumina_bank.accountservice.model.Account;
import com.lumina_bank.accountservice.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    boolean existsByCardNumber(String cardNumber);

    List<Card> findAllByAccount(Account account);

    @Query("""
    select a
    from Card c
    join c.account a
    where c.cardNumber = :cardNumber
""")
    Optional<Account> findAccountByCardNumber(@Param("cardNumber") String cardNumber);

    @Query("""
        select c
        from Card c
        join fetch c.account a
        where c.cardNumber = :cardNumber
    """)
    Optional<Card> findByCardNumberWithAccount(@Param("cardNumber") String cardNumber);

    List<Card> findAllByAccount_UserId(Long userId);

    Optional<Card> findFirstByAccountAndStatus(Account account, Status status);
}