package com.escrow.deal_service.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.escrow.deal_service.service.DealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ResolutionEventListener {
    private static final Logger log = LoggerFactory.getLogger(ResolutionEventListener.class);
    private final DealService dealService;
    private final ObjectMapper objectMapper;

    public ResolutionEventListener(DealService dealService, ObjectMapper objectMapper) {
        this.dealService = dealService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${app.kafka.resolution-topic}", groupId = "deal-service-resolution-group")
    public void onEvent(String raw) throws Exception {
        ResolutionEvent event = objectMapper.readValue(raw, ResolutionEvent.class);
        log.info("CONSUMED resolution event in deal-service type={} dealId={}", event.eventType(), event.dealId());
        switch (event.eventType()) {
            case "FUNDS_RELEASED" -> dealService.markReleased(event.dealId());
            case "FUNDS_REFUNDED" -> dealService.markRefunded(event.dealId());
            default -> { }
        }
    }
}
