package com.escrow.escrow_account_service.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.escrow.escrow_account_service.service.EscrowAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ResolutionEventListener {
    private static final Logger log = LoggerFactory.getLogger(ResolutionEventListener.class);
    private final EscrowAccountService service;
    private final ObjectMapper objectMapper;

    public ResolutionEventListener(EscrowAccountService service, ObjectMapper objectMapper) {
        this.service = service;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${app.kafka.resolution-topic}", groupId = "escrow-account-service-resolution-group")
    public void onResolutionEvent(String raw) throws Exception {
        ResolutionEvent event = objectMapper.readValue(raw, ResolutionEvent.class);
        log.info("CONSUMED resolution event in escrow-account-service type={} dealId={}", event.eventType(), event.dealId());
        if ("FUNDS_RELEASED".equals(event.eventType())) {
            service.markReleasedToBeneficiary(event.dealId());
        } else if ("FUNDS_REFUNDED".equals(event.eventType())) {
            service.markRefundedToDepositor(event.dealId());
        }
    }
}
