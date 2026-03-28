package com.escrow.deal_service.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.escrow.deal_service.service.DealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class FundingEventListener {

    private static final Logger log = LoggerFactory.getLogger(FundingEventListener.class);
    private final DealService dealService;
    private final ObjectMapper objectMapper;

    public FundingEventListener(DealService dealService, ObjectMapper objectMapper) {
        this.dealService = dealService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${app.kafka.funding-topic}", groupId = "deal-service-group")
    public void onFundingEvent(String raw) throws Exception {
        FundingEvent event = objectMapper.readValue(raw, FundingEvent.class);
        log.info("CONSUMED funding event in deal-service type={} dealId={}", event.eventType(), event.dealId());
        if ("FUNDING_PROCESSING".equals(event.eventType())) {
            dealService.markFundingProcessing(event.dealId());
        } else if ("FUNDS_SECURED".equals(event.eventType())) {
            dealService.markFundsSecured(event.dealId());
            dealService.markAwaitingBeneficiaryFulfillment(event.dealId());
        }
    }
}
