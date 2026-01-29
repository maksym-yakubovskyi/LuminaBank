package com.lumina_bank.frauddetectionservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_recipient_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRecipientStats {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String toCardNumber;

    @Column(nullable = false)
    private Integer usageCount;
    @Column(nullable = false)
    private LocalDateTime firstUseAt;
    @Column(nullable = false)
    private LocalDateTime lastUseAt;

    public void registerUsage(LocalDateTime occurredAt) {

        if (usageCount == null) {
            usageCount = 0;
        }

        usageCount++;

        if (firstUseAt == null) {
            firstUseAt = occurredAt;
        }

        lastUseAt = occurredAt;
    }

    public boolean isNewRecipient() {
        return usageCount != null && usageCount == 1;
    }
}
