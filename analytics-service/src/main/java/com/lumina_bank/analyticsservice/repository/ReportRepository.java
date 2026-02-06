package com.lumina_bank.analyticsservice.repository;

import com.lumina_bank.analyticsservice.enums.ReportStatus;
import com.lumina_bank.analyticsservice.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {
    Optional<Report> findByIdAndStatus(UUID reportId, ReportStatus reportStatus);

    List<Report> findAllByUserId(Long userId);
}
