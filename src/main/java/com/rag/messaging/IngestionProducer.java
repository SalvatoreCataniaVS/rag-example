package com.rag.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class IngestionProducer {

    @Inject
    @Channel("document-ingestion-out")
    Emitter<String> emitter;

    @Inject
    ObjectMapper objectMapper;

    public void send(DocumentIngestionEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            emitter.send(payload);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize ingestion event", e);
        }
    }

}