package com.lumina_bank.accountservice.enums;

public enum LoanStatus {

    /**
     * Кредит створено, але ще не розглянуто
     */
    REQUESTED,

    /**
     * Проходить скоринг / аналіз ризику
     */
    UNDER_REVIEW,

    /**
     * Кредит схвалено, але ще не видано
     */
    APPROVED,

    /**
     * Гроші зараховані на кредитний рахунок
     * Кредит активний
     */
    ACTIVE,

    /**
     * Повністю погашений
     */
    CLOSED,

    /**
     * Прострочений (є хоча б один overdue платіж)
     */
    OVERDUE,

    /**
     * Реструктуризований
     */
    RESTRUCTURED,

    /**
     * Дефолт (критична заборгованість)
     */
    DEFAULTED,

    /**
     * Відхилений
     */
    REJECTED,

    /**
     * Скасований до видачі
     */
    CANCELLED
}
