package com.lumina_bank.frauddetectionservice.sevice;

import com.lumina_bank.common.dto.event.payment_events.PaymentCreatedEvent;
import com.lumina_bank.frauddetectionservice.model.UserPaymentActivity;
import com.lumina_bank.frauddetectionservice.repository.UserPaymentActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {
    private final UserPaymentActivityRepository repository;

    @Transactional
    public void registerActivity(PaymentCreatedEvent event){
        UserPaymentActivity activity = UserPaymentActivity.builder()
                .userId(event.userId())
                .occurredAt(event.createdAt())
                .build();

        repository.save(activity);

        log.debug(
                "Payment activity registered: userId={}, time={}",
                event.userId(),
                event.createdAt()
        );
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanupOldActivity() {
        LocalDateTime threshold = LocalDateTime.now().minus(Duration.ofHours(1));

        int deleted = repository.deleteOlderThan(threshold);

        log.debug("Cleaned {} old activity records", deleted);
    }
}