package com.lumina_bank.frauddetectionservice.sevice;

import com.lumina_bank.common.dto.event.payment_events.PaymentCreatedEvent;
import com.lumina_bank.frauddetectionservice.model.UserRecipientStats;
import com.lumina_bank.frauddetectionservice.repository.UserRecipientStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipienceService {
    private final UserRecipientStatsRepository repository;

    @Transactional
    public void registerRecipient(PaymentCreatedEvent event){
        UserRecipientStats stats = repository
                .findByUserIdAndToCardNumber(
                        event.userId(),
                        event.toCardNumber()
                )
                .orElseGet(() ->
                        UserRecipientStats.builder()
                                .userId(event.userId())
                                .toCardNumber(event.toCardNumber())
                                .build()
                );

        stats.registerUsage(event.createdAt());

        repository.save(stats);

        log.debug(
                "Recipient stats updated: userId={}, toCard={}, usageCount={}",
                event.userId(),
                event.toCardNumber(),
                stats.getUsageCount()
        );
    }
}
