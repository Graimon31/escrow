package com.escrow.fulfillment_service.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class FulfillmentEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(FulfillmentEventPublisher.class);

    private final KafkaTemplate<String, FulfillmentEvent> kafkaTemplate;
    private final String topic;

    public FulfillmentEventPublisher(KafkaTemplate<String, FulfillmentEvent> kafkaTemplate,
                                     @Value("${app.kafka.fulfillment-topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(FulfillmentEvent event) {
        kafkaTemplate.send(topic, event.dealId().toString(), event);
        log.info("PUBLISHED fulfillment event type={} dealId={} topic={}", event.eventType(), event.dealId(), topic);
    }
}
