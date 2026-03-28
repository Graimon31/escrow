package com.escrow.escrow_account_service;

import com.escrow.escrow_account_service.domain.EscrowAccountState;
import com.escrow.escrow_account_service.service.EscrowAccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:escrowacc;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.kafka.bootstrap-servers=localhost:9092",
        "spring.kafka.listener.auto-startup=false"
})
class EscrowAccountServiceTest {

    @Autowired
    private EscrowAccountService service;

    @Test
    void shouldOpenAccountAndMoveToAwaitingDeposit() {
        UUID dealId = UUID.randomUUID();
        var account = service.open(dealId, new BigDecimal("100.00"), "RUB");
        assertEquals(EscrowAccountState.AWAITING_DEPOSIT, account.getState());
        assertEquals(dealId, account.getDealId());

        service.markDepositInProcess(dealId);
        assertEquals(EscrowAccountState.DEPOSIT_IN_PROCESS, service.byDeal(dealId).getState());

        service.markHeldInEscrow(dealId);
        assertEquals(EscrowAccountState.HELD_IN_ESCROW, service.byDeal(dealId).getState());
    }

    @Test
    void shouldRejectSecondAccountForSameDeal() {
        UUID dealId = UUID.randomUUID();
        service.open(dealId, new BigDecimal("100.00"), "RUB");
        assertThrows(IllegalStateException.class, () -> service.open(dealId, new BigDecimal("100.00"), "RUB"));
    }
}
