package com.lumina_bank.frauddetectionservice.sevice;

import com.lumina_bank.common.dto.event.payment_events.PaymentCreatedEvent;
import com.lumina_bank.frauddetectionservice.model.UserFraudProfile;
import com.lumina_bank.frauddetectionservice.repository.UserFraudProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {
    private final UserFraudProfileRepository repository;

    @Transactional
    public void registerPaymentAttempt(PaymentCreatedEvent event){
        UserFraudProfile profile = repository.findById(event.userId())
                .orElseGet(() ->
                        UserFraudProfile.builder()
                                .id(event.userId())
                                .totalPayments(0)
                                .build()
                );

        profile.registerNewPayment(
                event.amount(),
                event.createdAt()
        );

        repository.save(profile);

        log.debug("Updated fraud profile for userId={}, totalPayments={}",
                event.userId(), profile.getTotalPayments());
    }
}
