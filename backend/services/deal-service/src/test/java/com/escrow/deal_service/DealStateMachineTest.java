package com.escrow.deal_service;

import com.escrow.deal_service.domain.DealState;
import com.escrow.deal_service.service.DealStateMachine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DealStateMachineTest {

    @Test
    void validHappyPathShouldPass() {
        DealState state = DealState.DRAFT;
        state = DealStateMachine.agree(state);
        assertEquals(DealState.AGREED, state);
        state = DealStateMachine.openAccount(state);
        assertEquals(DealState.ACCOUNT_OPENED, state);
        state = DealStateMachine.awaitFunding(state);
        assertEquals(DealState.AWAITING_FUNDING, state);
        state = DealStateMachine.fundingProcessing(state);
        assertEquals(DealState.FUNDING_PROCESSING, state);
        state = DealStateMachine.fundsSecured(state);
        assertEquals(DealState.FUNDS_SECURED, state);
    }

    @Test
    void forbiddenTransitionsShouldFail() {
        assertThrows(IllegalStateException.class, () -> DealStateMachine.agree(DealState.AGREED));
        assertThrows(IllegalStateException.class, () -> DealStateMachine.openAccount(DealState.DRAFT));
        assertThrows(IllegalStateException.class, () -> DealStateMachine.awaitFunding(DealState.AGREED));
        assertThrows(IllegalStateException.class, () -> DealStateMachine.fundingProcessing(DealState.DRAFT));
        assertThrows(IllegalStateException.class, () -> DealStateMachine.fundsSecured(DealState.AWAITING_FUNDING));
    }
}
