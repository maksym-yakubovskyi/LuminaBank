package com.lumina_bank.frauddetectionservice.domain.risk;

public record RiskCheckResult(int score, String reason) {

    public static RiskCheckResult none() {
        return new RiskCheckResult(0, null);
    }

    public boolean triggered() {
        return score > 0;
    }
}
