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
        state = DealStateMachine.openAccount(state);
        state = DealStateMachine.awaitFunding(state);
        state = DealStateMachine.fundingProcessing(state);
        state = DealStateMachine.fundsSecured(state);
        state = DealStateMachine.awaitingDepositorReview(state);
        state = DealStateMachine.releasePending(state);
        assertEquals(DealState.RELEASE_PENDING, state);
    }

    @Test
    void correctionPathShouldReturnToBeneficiary() {
        DealState state = DealState.FUNDS_SECURED;
        state = DealStateMachine.awaitingDepositorReview(state);
        state = DealStateMachine.awaitingBeneficiaryFulfillment(state);
        assertEquals(DealState.AWAITING_BENEFICIARY_FULFILLMENT, state);
    }

    @Test
    void forbiddenTransitionsShouldFail() {
        assertThrows(IllegalStateException.class, () -> DealStateMachine.releasePending(DealState.DRAFT));
        assertThrows(IllegalStateException.class, () -> DealStateMachine.disputed(DealState.FUNDS_SECURED));
    }
}
