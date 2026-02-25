package com.lumina_bank.notificationservice.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_contact_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserContactInfo {

    @Id
    private Long userId;

    private String email;

    private LocalDateTime updatedAt;
}
