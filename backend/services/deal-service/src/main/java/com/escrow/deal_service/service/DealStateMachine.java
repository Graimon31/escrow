package com.escrow.deal_service.service;

import com.escrow.deal_service.domain.DealState;

public final class DealStateMachine {
    private DealStateMachine() {}

    public static DealState agree(DealState current) {
        if (current != DealState.DRAFT) {
            throw new IllegalStateException("Сделку можно согласовать только из состояния DRAFT");
        }
        return DealState.AGREED;
    }

    public static DealState openAccount(DealState current) {
        if (current != DealState.AGREED) {
            throw new IllegalStateException("Счёт эскроу можно открыть только из состояния AGREED");
        }
        return DealState.ACCOUNT_OPENED;
    }

    public static DealState awaitFunding(DealState current) {
        if (current != DealState.ACCOUNT_OPENED) {
            throw new IllegalStateException("Ожидание фондирования возможно только после ACCOUNT_OPENED");
        }
        return DealState.AWAITING_FUNDING;
    }

    public static DealState fundingProcessing(DealState current) {
        if (current != DealState.AWAITING_FUNDING) {
            throw new IllegalStateException("FUNDING_PROCESSING возможно только из AWAITING_FUNDING");
        }
        return DealState.FUNDING_PROCESSING;
    }

    public static DealState fundsSecured(DealState current) {
        if (current != DealState.FUNDING_PROCESSING) {
            throw new IllegalStateException("FUNDS_SECURED возможно только из FUNDING_PROCESSING");
        }
        return DealState.FUNDS_SECURED;
    }
}
