package com.escrow.deal_service.event;

import com.escrow.deal_service.service.DealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class FundingEventListener {

    private static final Logger log = LoggerFactory.getLogger(FundingEventListener.class);
    private final DealService dealService;

    public FundingEventListener(DealService dealService) {
        this.dealService = dealService;
    }

    @KafkaListener(topics = "${app.kafka.funding-topic}", groupId = "deal-service-group")
    public void onFundingEvent(FundingEvent event) {
        log.info("CONSUMED funding event in deal-service type={} dealId={}", event.eventType(), event.dealId());
        if ("FUNDING_PROCESSING".equals(event.eventType())) {
            dealService.markFundingProcessing(event.dealId());
        } else if ("FUNDS_SECURED".equals(event.eventType())) {
            dealService.markFundsSecured(event.dealId());
        }
    }
}
