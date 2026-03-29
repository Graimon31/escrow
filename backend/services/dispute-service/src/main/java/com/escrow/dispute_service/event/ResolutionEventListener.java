package com.escrow.dispute_service.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.escrow.dispute_service.service.DisputeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ResolutionEventListener {
    private static final Logger log = LoggerFactory.getLogger(ResolutionEventListener.class);
    private final DisputeService service;
    private final ObjectMapper objectMapper;

    public ResolutionEventListener(DisputeService service, ObjectMapper objectMapper) {
        this.service = service;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${app.kafka.resolution-topic}", groupId = "dispute-service-resolution-group")
    public void onEvent(String raw) throws Exception {
        ResolutionEvent event = objectMapper.readValue(raw, ResolutionEvent.class);
        log.info("CONSUMED resolution event in dispute-service type={} dealId={}", event.eventType(), event.dealId());
        switch (event.eventType()) {
            case "FUNDS_RELEASED", "FUNDS_REFUNDED" -> service.markResolved(event.dealId(), event.actor(), event.comment());
            default -> { }
        }
    }
}
