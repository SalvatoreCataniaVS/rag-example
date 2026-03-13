package com.rag.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rag.interceptor.model.LogEntry;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.time.Instant;

@Provider
public class RestClientLoggingInterceptor implements ClientRequestFilter, ClientResponseFilter {

    private static final Logger LOG = Logger.getLogger(RestClientLoggingInterceptor.class);

    private static final String START_TIME_KEY = "rag.client.startTime";

    @Inject
    ObjectMapper objectMapper;

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        // Record start time for duration calculation
        requestContext.setProperty(START_TIME_KEY, System.currentTimeMillis());

        // Serialize outgoing payload
        String payload = null;
        if (requestContext.hasEntity()) {
            try {
                payload = objectMapper.writeValueAsString(requestContext.getEntity());
            } catch (Exception e) {
                payload = "[unserializable]";
            }
        }

        // Build and log entry
        LogEntry entry = LogEntry.builder()
                .timestamp(Instant.now())
                .phase("RestClientRequest")
                .method(requestContext.getMethod())
                .targetUrl(requestContext.getUri().toString())
                .payload(payload)
                .build();

        log(entry);
    }

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        // Calculate request duration
        Long startTime = (Long) requestContext.getProperty(START_TIME_KEY);
        long durationMs = startTime != null ? System.currentTimeMillis() - startTime : -1;

        // Build and log entry
        LogEntry entry = LogEntry.builder()
                .timestamp(Instant.now())
                .phase("RestClientResponse")
                .method(requestContext.getMethod())
                .targetUrl(requestContext.getUri().toString())
                .status(responseContext.getStatus())
                .durationMs(durationMs)
                .build();

        log(entry);
    }

    // ─── Private ──────────────────────────────────────────────────────────────

    private void log(LogEntry entry) {
        try {
            LOG.info(objectMapper.writeValueAsString(entry));
        } catch (Exception e) {
            LOG.warnf("Failed to serialize log entry: %s", e.getMessage());
        }
    }

}