package com.escrow.review_service;

import com.escrow.review_service.domain.ReviewActionType;
import com.escrow.review_service.event.ReviewEventPublisher;
import com.escrow.review_service.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:review;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=none"
})
class ReviewServiceTest {

    @Autowired
    private ReviewService service;

    @MockBean
    private ReviewEventPublisher publisher;

    @Test
    void shouldStoreActionHistory() {
        UUID dealId = UUID.randomUUID();
        service.act(dealId, ReviewActionType.CORRECTION, "depositor", "Нужна доработка");
        service.act(dealId, ReviewActionType.ACCEPT, "depositor", "Теперь принято");

        assertEquals(2, service.history(dealId).size());
        assertEquals(ReviewActionType.CORRECTION, service.history(dealId).get(0).getActionType());
    }
}
