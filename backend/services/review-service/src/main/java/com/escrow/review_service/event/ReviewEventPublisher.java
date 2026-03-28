package com.escrow.review_service.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ReviewEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ReviewEventPublisher.class);
    private final KafkaTemplate<String, ReviewEvent> kafkaTemplate;
    private final String topic;

    public ReviewEventPublisher(KafkaTemplate<String, ReviewEvent> kafkaTemplate,
                                @Value("${app.kafka.review-topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(ReviewEvent event) {
        kafkaTemplate.send(topic, event.dealId().toString(), event);
        log.info("PUBLISHED review event type={} dealId={} topic={}", event.eventType(), event.dealId(), topic);
    }
}
