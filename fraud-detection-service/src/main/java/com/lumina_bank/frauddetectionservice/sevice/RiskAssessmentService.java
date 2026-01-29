package com.lumina_bank.frauddetectionservice.sevice;

import com.lumina_bank.common.dto.event.payment_events.PaymentCreatedEvent;
import com.lumina_bank.common.dto.event.payment_events.PaymentRiskResultEvent;
import com.lumina_bank.common.enums.payment.RiskLevel;
import com.lumina_bank.frauddetectionservice.dto.RiskCheckResult;
import com.lumina_bank.frauddetectionservice.dto.RiskRule;
import com.lumina_bank.frauddetectionservice.model.PaymentRiskAssessment;
import com.lumina_bank.frauddetectionservice.repository.PaymentRiskAssessmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiskAssessmentService {
    private final List<RiskRule> rules;
    private final PaymentRiskAssessmentRepository repository;

    @Transactional
    public PaymentRiskResultEvent assess(PaymentCreatedEvent event){
        int totalScore = 0;

        List<String> reasons = new ArrayList<>();

        for (RiskRule rule : rules) {
            RiskCheckResult result = rule.check(event);
            if(result.triggered()){
                totalScore += result.score();
                reasons.add(result.reason());
            }
        }

        RiskLevel level = RiskLevel.fromScore(totalScore);

        repository.save(
                PaymentRiskAssessment.builder()
                        .id(event.paymentId())
                        .riskScore(totalScore)
                        .riskLevel(level)
                        .reasons(String.join(",", reasons))
                        .build()
        );

        return new PaymentRiskResultEvent(
                event.paymentId(),
                totalScore,
                level,
                reasons
        );
    }
}
