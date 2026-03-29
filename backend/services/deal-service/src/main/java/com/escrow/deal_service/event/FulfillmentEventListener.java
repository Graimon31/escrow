package com.escrow.deal_service.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.escrow.deal_service.service.DealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class FulfillmentEventListener {
    private static final Logger log = LoggerFactory.getLogger(FulfillmentEventListener.class);
    private final DealService dealService;
    private final ObjectMapper objectMapper;

    public FulfillmentEventListener(DealService dealService, ObjectMapper objectMapper) {
        this.dealService = dealService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${app.kafka.fulfillment-topic}", groupId = "deal-service-fulfillment-group")
    public void onEvent(String raw) throws Exception {
        FulfillmentEvent event = objectMapper.readValue(raw, FulfillmentEvent.class);
        log.info("CONSUMED fulfillment event in deal-service type={} dealId={}", event.eventType(), event.dealId());
        if ("FULFILLMENT_SUBMITTED".equals(event.eventType())) {
            dealService.markAwaitingDepositorReview(event.dealId());
        }
    }
}
