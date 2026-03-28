package com.escrow.fulfillment_service;

import com.escrow.fulfillment_service.api.FulfillmentDtos;
import com.escrow.fulfillment_service.event.FulfillmentEventPublisher;
import com.escrow.fulfillment_service.service.FulfillmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:fulfillment;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=none"
})
class FulfillmentServiceTest {

    @Autowired
    private FulfillmentService service;

    @MockBean
    private FulfillmentEventPublisher publisher;

    @Test
    void shouldSubmitFulfillmentWithDocuments() {
        UUID dealId = UUID.randomUUID();
        var record = service.submit(
                dealId,
                "beneficiary",
                "Обязательство исполнено",
                List.of(new FulfillmentDtos.DocumentMetaRequest("proof.pdf", "application/pdf", 1234L))
        );
        assertEquals("SUBMITTED", record.getStatus());
        assertEquals(1, service.documents(dealId).size());
    }
}
