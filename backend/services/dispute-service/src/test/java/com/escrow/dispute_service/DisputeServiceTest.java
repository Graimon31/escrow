package com.escrow.dispute_service;

import com.escrow.dispute_service.domain.DisputeStatus;
import com.escrow.dispute_service.event.DisputeEventPublisher;
import com.escrow.dispute_service.service.DisputeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:dispute;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.kafka.bootstrap-servers=localhost:9092"
})
class DisputeServiceTest {

    @Autowired
    private DisputeService service;

    @MockBean
    private DisputeEventPublisher publisher;

    @Test
    void shouldOpenAndResolveDispute() {
        UUID dealId = UUID.randomUUID();
        var dispute = service.open(dealId, "depositor-1", "quality issue");
        assertEquals(DisputeStatus.OPEN, dispute.getStatus());

        service.markResolved(dealId, "operator-1", "resolved by refund");
        assertEquals(DisputeStatus.RESOLVED, service.history(dealId).get(0).getStatus());
    }

    @Test
    void shouldPreventSecondOpenDispute() {
        UUID dealId = UUID.randomUUID();
        service.open(dealId, "depositor-1", "first");
        assertThrows(IllegalStateException.class, () -> service.open(dealId, "depositor-2", "second"));
    }
}
