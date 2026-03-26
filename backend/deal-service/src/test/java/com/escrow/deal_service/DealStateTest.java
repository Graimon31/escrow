package com.escrow.deal_service;

import com.escrow.deal_service.domain.DealState;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DealStateTest {
  @Test
  void enumContainsCoreStates() {
    assertNotNull(DealState.valueOf("DRAFT"));
    assertNotNull(DealState.valueOf("RELEASED"));
  }
}
