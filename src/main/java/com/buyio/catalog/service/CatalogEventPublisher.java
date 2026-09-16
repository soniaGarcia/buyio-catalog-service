package com.buyio.catalog.service;

import com.buyio.catalog.dto.CatalogEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic-catalog}")
    private String catalogTopic;

    public void publishEvent(CatalogEvent event) {
        log.info("Publicando evento Kafka [{}] a tópico: {}", event.getEventType(), catalogTopic);
        kafkaTemplate.send(catalogTopic, event.getProductId().toString(), event);
    }
}