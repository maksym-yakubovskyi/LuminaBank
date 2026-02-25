package com.lumina_bank.paymentservice.domain.repository;

import com.lumina_bank.common.enums.payment.Currency;
import com.lumina_bank.paymentservice.domain.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    Optional<ExchangeRate> findTopByBaseCurrencyAndTargetCurrencyOrderByDateDesc(Currency from, Currency to);
}
