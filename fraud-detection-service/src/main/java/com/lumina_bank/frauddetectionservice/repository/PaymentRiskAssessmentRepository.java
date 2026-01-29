package com.lumina_bank.frauddetectionservice.repository;

import com.lumina_bank.frauddetectionservice.model.PaymentRiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRiskAssessmentRepository extends JpaRepository<PaymentRiskAssessment,Long> {
}
