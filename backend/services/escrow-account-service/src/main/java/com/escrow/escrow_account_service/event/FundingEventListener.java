package com.escrow.escrow_account_service.event;

import com.escrow.escrow_account_service.service.EscrowAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class FundingEventListener {

    private static final Logger log = LoggerFactory.getLogger(FundingEventListener.class);
    private final EscrowAccountService service;

    public FundingEventListener(EscrowAccountService service) {
        this.service = service;
    }

    @KafkaListener(topics = "${app.kafka.funding-topic}", groupId = "escrow-account-service-group")
    public void onFundingEvent(FundingEvent event) {
        log.info("CONSUMED funding event in escrow-account-service type={} dealId={}", event.eventType(), event.dealId());
        if ("FUNDING_PROCESSING".equals(event.eventType())) {
            service.markDepositInProcess(event.dealId());
        } else if ("FUNDS_SECURED".equals(event.eventType())) {
            service.markHeldInEscrow(event.dealId());
        }
    }
}
