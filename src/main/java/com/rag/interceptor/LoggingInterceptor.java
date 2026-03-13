package com.rag.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rag.interceptor.model.LogEntry;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.*;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;

@Provider
@PreMatching
public class LoggingInterceptor implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(LoggingInterceptor.class);

    // Property key used to store request start time across filters
    private static final String START_TIME_KEY = "rag.request.startTime";

    @Inject
    ObjectMapper objectMapper;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Record start time for duration calculation in response filter
        requestContext.setProperty(START_TIME_KEY, System.currentTimeMillis());

        // Read and restore payload — InputStream can only be read once
        String payload = readAndRestoreBody(requestContext);

        // Extract security context info
        boolean authenticated = requestContext.getSecurityContext() != null
                && requestContext.getSecurityContext().getUserPrincipal() != null;
        String userId = authenticated
                ? requestContext.getSecurityContext().getUserPrincipal().getName()
                : null;

        // Extract session id from header if present
        String sessionId = requestContext.getHeaderString("X-Session-Id");

        // Extract caller IP
        String remoteAddress = requestContext.getHeaderString("X-Forwarded-For");

        // Build and log entry
        LogEntry entry = LogEntry.builder()
                .timestamp(Instant.now())
                .phase("RestRequest")
                .method(requestContext.getMethod())
                .path(requestContext.getUriInfo().getRequestUri().toString())
                .userId(userId)
                .sessionId(sessionId)
                .authenticated(authenticated)
                .remoteAddress(remoteAddress)
                .payload(payload)
                .build();

        log(entry);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        // Calculate request duration
        Long startTime = (Long) requestContext.getProperty(START_TIME_KEY);
        long durationMs = startTime != null ? System.currentTimeMillis() - startTime : -1;

        // Extract security context info
        boolean authenticated = requestContext.getSecurityContext() != null
                && requestContext.getSecurityContext().getUserPrincipal() != null;
        String userId = authenticated
                ? requestContext.getSecurityContext().getUserPrincipal().getName()
                : null;

        // Serialize response payload
        String payload = serializeResponsePayload(responseContext);

        // Build and log entry
        LogEntry entry = LogEntry.builder()
                .timestamp(Instant.now())
                .phase("RestResponse")
                .method(requestContext.getMethod())
                .path(requestContext.getUriInfo().getRequestUri().toString())
                .userId(userId)
                .authenticated(authenticated)
                .status(responseContext.getStatus())
                .durationMs(durationMs)
                .payload(payload)
                .build();

        log(entry);
    }

    // ─── Private ──────────────────────────────────────────────────────────────

    private String readAndRestoreBody(ContainerRequestContext requestContext) throws IOException {
        if (!requestContext.hasEntity()) {
            return null;
        }

        // Read the body bytes
        byte[] bodyBytes;
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            requestContext.getEntityStream().transferTo(buffer);
            bodyBytes = buffer.toByteArray();
        }

        // Restore the stream so JAX-RS can still read it downstream
        requestContext.setEntityStream(new ByteArrayInputStream(bodyBytes));

        return new String(bodyBytes);
    }

    private String serializeResponsePayload(ContainerResponseContext responseContext) {
        if (responseContext.getEntity() == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(responseContext.getEntity());
        } catch (Exception e) {
            return "[unserializable]";
        }
    }

    private void log(LogEntry entry) {
        try {
            LOG.info(objectMapper.writeValueAsString(entry));
        } catch (Exception e) {
            LOG.warnf("Failed to serialize log entry: %s", e.getMessage());
        }
    }

}