package com.lumina_bank.paymentservice.repository;

import com.lumina_bank.common.enums.payment.Currency;
import com.lumina_bank.paymentservice.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Integer> {
    Optional<ExchangeRate> findTopByBaseCurrencyAndTargetCurrencyOrderByDateDesc(Currency from, Currency to);
}
