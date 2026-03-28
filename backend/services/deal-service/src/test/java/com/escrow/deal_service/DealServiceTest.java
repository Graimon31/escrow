package com.escrow.deal_service;

import com.escrow.deal_service.client.EscrowAccountClient;
import com.escrow.deal_service.domain.DealState;
import com.escrow.deal_service.service.DealService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:dealdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.kafka.bootstrap-servers=localhost:9092",
        "spring.kafka.listener.auto-startup=false"
})
class DealServiceTest {

    @Autowired
    private DealService dealService;

    @MockBean
    private EscrowAccountClient escrowAccountClient;

    @Test
    void shouldGoDraftToAwaitingFunding() {
        var deal = dealService.create("Тест", new BigDecimal("500.00"), "RUB", "depositor", "beneficiary");
        assertEquals(DealState.DRAFT, deal.getState());

        deal = dealService.agree(deal.getId());
        assertEquals(DealState.AGREED, deal.getState());

        deal = dealService.openEscrowAccount(deal.getId(), "token");
        assertEquals(DealState.AWAITING_FUNDING, deal.getState());

        dealService.markFundingProcessing(deal.getId());
        assertEquals(DealState.FUNDING_PROCESSING, dealService.get(deal.getId()).getState());

        dealService.markFundsSecured(deal.getId());
        assertEquals(DealState.FUNDS_SECURED, dealService.get(deal.getId()).getState());
    }

    @Test
    void shouldRejectAgreeFromWrongState() {
        var deal = dealService.create("Тест", new BigDecimal("500.00"), "RUB", "depositor", "beneficiary");
        dealService.agree(deal.getId());
        assertThrows(IllegalStateException.class, () -> dealService.agree(deal.getId()));
    }
}
