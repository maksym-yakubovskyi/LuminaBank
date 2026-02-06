package com.lumina_bank.analyticsservice.model;

import com.lumina_bank.analyticsservice.enums.ReportFormat;
import com.lumina_bank.analyticsservice.enums.ReportStatus;
import com.lumina_bank.analyticsservice.enums.ReportType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "report")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {
    @Id
    private UUID id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private ReportType reportType;
    @Enumerated(EnumType.STRING)
    private ReportFormat format;
    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    private String filePath;
    private String fileName;
    private MediaType contentType;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;
}
