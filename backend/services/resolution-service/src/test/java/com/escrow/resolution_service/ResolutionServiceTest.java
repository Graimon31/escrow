package com.escrow.resolution_service;

import com.escrow.resolution_service.domain.ResolutionOutcome;
import com.escrow.resolution_service.event.ResolutionEventPublisher;
import com.escrow.resolution_service.service.ResolutionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:resolution;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.kafka.bootstrap-servers=localhost:9092"
})
class ResolutionServiceTest {

    @Autowired
    private ResolutionService service;

    @MockBean
    private ResolutionEventPublisher publisher;

    @Test
    void shouldCreateReleaseDecisionAndHistory() {
        UUID dealId = UUID.randomUUID();
        var decision = service.resolve(dealId, ResolutionOutcome.RELEASE, "operator-1", "release approved");
        assertEquals(ResolutionOutcome.RELEASE, decision.getOutcome());
        assertFalse(service.history(dealId).isEmpty());
    }

    @Test
    void shouldCreateRefundDecision() {
        UUID dealId = UUID.randomUUID();
        var decision = service.resolve(dealId, ResolutionOutcome.REFUND, "operator-1", "refund approved");
        assertEquals(ResolutionOutcome.REFUND, decision.getOutcome());
    }
}
