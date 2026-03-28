package com.escrow.funding_service;

import com.escrow.funding_service.domain.FundingOperationRepository;
import com.escrow.funding_service.domain.FundingStatus;
import com.escrow.funding_service.service.FundingService;
import com.escrow.funding_service.event.FundingEventPublisher;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:funding;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.kafka.bootstrap-servers=localhost:9092"
})
class FundingServiceTest {

    @Autowired
    private FundingService fundingService;

    @Autowired
    private FundingOperationRepository repository;

    @MockBean
    private FundingEventPublisher fundingEventPublisher;

    @Test
    void shouldCompleteMockFundingFlowToFundsSecured() {
        UUID dealId = UUID.randomUUID();
        var op = fundingService.deposit(dealId, new BigDecimal("100.00"), "RUB", "depositor", "key-1");
        assertEquals(FundingStatus.FUNDS_SECURED, op.getStatus());
        assertEquals(2, fundingService.auditTrail(dealId).size());
    }

    @Test
    void shouldSupportIdempotency() {
        UUID dealId = UUID.randomUUID();
        var op1 = fundingService.deposit(dealId, new BigDecimal("100.00"), "RUB", "depositor", "same-key");
        var op2 = fundingService.deposit(dealId, new BigDecimal("100.00"), "RUB", "depositor", "same-key");
        assertEquals(op1.getId(), op2.getId());
    }
}
