package com.escrow.deal_service.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.escrow.deal_service.service.DealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ReviewEventListener {
    private static final Logger log = LoggerFactory.getLogger(ReviewEventListener.class);
    private final DealService dealService;
    private final ObjectMapper objectMapper;

    public ReviewEventListener(DealService dealService, ObjectMapper objectMapper) {
        this.dealService = dealService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${app.kafka.review-topic}", groupId = "deal-service-review-group")
    public void onEvent(String raw) throws Exception {
        ReviewEvent event = objectMapper.readValue(raw, ReviewEvent.class);
        log.info("CONSUMED review event in deal-service type={} dealId={}", event.eventType(), event.dealId());
        switch (event.eventType()) {
            case "REVIEW_ACCEPTED" -> dealService.markReleasePending(event.dealId());
            case "REVIEW_REJECTED", "CORRECTION_REQUESTED" -> dealService.markAwaitingBeneficiaryFulfillment(event.dealId());
            case "DISPUTE_OPENED" -> dealService.markDisputed(event.dealId());
            default -> { }
        }
    }
}
