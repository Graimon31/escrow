package com.escrow.funding_service.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class FundingEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(FundingEventPublisher.class);

    private final KafkaTemplate<String, FundingEvent> kafkaTemplate;
    private final String topic;

    public FundingEventPublisher(KafkaTemplate<String, FundingEvent> kafkaTemplate,
                                 @Value("${app.kafka.funding-topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(FundingEvent event) {
        kafkaTemplate.send(topic, event.dealId().toString(), event);
        log.info("PUBLISHED funding event type={} dealId={} topic={}", event.eventType(), event.dealId(), topic);
    }
}
