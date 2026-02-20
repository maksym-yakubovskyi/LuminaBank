package com.lumina_bank.accountservice.enums;

public enum InstallmentStatus {

    /**
     * Очікує настання дати платежу
     */
    PENDING,

    /**
     * Термін платежу настав
     */
    DUE,

    /**
     * Частково сплачений
     */
    PARTIALLY_PAID,

    /**
     * Повністю сплачений
     */
    PAID,

    /**
     * Прострочений
     */
    OVERDUE,

    /**
     * Списаний / закритий при достроковому погашенні
     */
    CLOSED
}

